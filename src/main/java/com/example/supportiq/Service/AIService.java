package com.example.supportiq.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AIService {

    private final ChatClient chatClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public AIService(ChatClient.Builder builder) {
        this.chatClient = builder.build();
    }

    // keep ALL your existing methods below unchanged

    // ========== TICKET CATEGORIZATION ==========

    /**
     * Categorize ticket into predefined categories using AI
     * @param title Ticket title
     * @param description Ticket description
     * @return Category: TECHNICAL, BILLING, FEATURE_REQUEST, BUG, or UNCATEGORIZED
     */
    public String categorizeTicket(String title, String description) {
        try {
            String prompt = String.format("""
                Analyze this support ticket and categorize it.
                
                Ticket Title: %s
                Ticket Description: %s
                
                Valid Categories: TECHNICAL, BILLING, FEATURE_REQUEST, BUG
                
                Return ONLY the category name, nothing else.
                If unsure, return UNCATEGORIZED.
                """, title, description);

            String response = chatClient.prompt()
                    .user(prompt)
                    .call()
                    .content()
                    .trim();

            // Validate response
            if (response.matches("(?i)(TECHNICAL|BILLING|FEATURE_REQUEST|BUG|UNCATEGORIZED)")) {
                return response.toUpperCase();
            }

            log.warn("Invalid category response: {}", response);
            return "UNCATEGORIZED";

        } catch (Exception e) {
            log.error("Error categorizing ticket", e);
            return "UNCATEGORIZED";
        }
    }

    // ========== SENTIMENT ANALYSIS ==========

    /**
     * Analyze sentiment of ticket description
     * @param description Ticket description
     * @return SentimentResult with sentiment type and score
     */
    public SentimentResult analyzeSentiment(String description) {
        try {
            String prompt = String.format("""
                Analyze the sentiment of this customer message.
                
                Message: %s
                
                Respond in JSON format (valid JSON only):
                {
                  "sentiment": "POSITIVE/NEUTRAL/NEGATIVE/ANGRY",
                  "score": (a number between 0.0 and 1.0),
                  "urgency": "LOW/MEDIUM/HIGH"
                }
                
                Return only valid JSON, no other text.
                """, description);

            String response = chatClient.prompt()
                    .user(prompt)
                    .call()
                    .content()
                    .trim();

            // Parse JSON response
            SentimentResult result = objectMapper.readValue(response, SentimentResult.class);

            // Validate values
            if (result.getSentiment() == null) {
                result.setSentiment("NEUTRAL");
            }
            if (result.getScore() == null || result.getScore() < 0 || result.getScore() > 1) {
                result.setScore(0.5);
            }
            if (result.getUrgency() == null) {
                result.setUrgency("LOW");
            }

            return result;

        } catch (Exception e) {
            log.error("Error analyzing sentiment", e);
            // Return default sentiment on error
            return SentimentResult.builder()
                    .sentiment("NEUTRAL")
                    .score(0.5)
                    .urgency("LOW")
                    .build();
        }
    }

    // ========== AI RESPONSE SUGGESTIONS ==========

    /**
     * Generate AI-suggested response for ticket
     * @param category Ticket category
     * @param sentiment Ticket sentiment
     * @param description Ticket description
     * @return Suggested response text
     */
    public String suggestResponse(String category, String sentiment, String description) {
        try {
            String prompt = String.format("""
                You are a professional customer support agent.
                
                Ticket Category: %s
                Customer Sentiment: %s
                Customer Issue: %s
                
                Write a professional, empathetic response that:
                1. Acknowledges the customer's issue
                2. Shows understanding and empathy
                3. Provides a solution or next steps
                4. Maintains a friendly and professional tone
                5. Is under 200 words
                
                For ANGRY sentiment, be extra empathetic and apologetic.
                For POSITIVE sentiment, acknowledge their satisfaction.
                
                Response:
                """, category, sentiment, description);

            String response = chatClient.prompt()
                    .user(prompt)
                    .call()
                    .content()
                    .trim();

            // Ensure response is not too long
            if (response.length() > 1000) {
                response = response.substring(0, 1000) + "...";
            }

            return response;

        } catch (Exception e) {
            log.error("Error generating response suggestion", e);
            return "Thank you for contacting us. We appreciate your feedback and will get back to you shortly.";
        }
    }

    // ========== RECURRING ISSUE DETECTION ==========

    /**
     * Analyze text to detect if it's related to a recurring issue
     * @param description Ticket description
     * @return Issue analysis
     */
    public IssueAnalysis analyzeIssue(String description) {
        try {
            String prompt = String.format("""
                Analyze this customer issue and identify the main problem.
                
                Issue: %s
                
                Respond in JSON format (valid JSON only):
                {
                  "mainTopic": "Brief topic (e.g., Login Issue, Payment Error)",
                  "severity": "LOW/MEDIUM/HIGH",
                  "affectsMultiple": true/false,
                  "rootCause": "Possible root cause if identifiable"
                }
                
                Return only valid JSON, no other text.
                """, description);

            String response = chatClient.prompt()
                    .user(prompt)
                    .call()
                    .content()
                    .trim();

            return objectMapper.readValue(response, IssueAnalysis.class);

        } catch (Exception e) {
            log.error("Error analyzing issue", e);
            return IssueAnalysis.builder()
                    .mainTopic("Unknown")
                    .severity("MEDIUM")
                    .affectsMultiple(false)
                    .rootCause("Unable to determine")
                    .build();
        }
    }

    // ========== INNER CLASSES FOR RESPONSES ==========

    @lombok.Data
    @lombok.AllArgsConstructor
    @lombok.NoArgsConstructor
    @lombok.Builder
    public static class SentimentResult {
        private String sentiment; // POSITIVE, NEUTRAL, NEGATIVE, ANGRY
        private Double score;     // 0.0 to 1.0
        private String urgency;   // LOW, MEDIUM, HIGH
    }

    @lombok.Data
    @lombok.AllArgsConstructor
    @lombok.NoArgsConstructor
    @lombok.Builder
    public static class IssueAnalysis {
        private String mainTopic;
        private String severity;
        private Boolean affectsMultiple;
        private String rootCause;
    }
}