import Constants from "expo-constants";
import { Alert } from "react-native";

const API_URL = process.env.EXPO_PUBLIC_API_URL;

export const fetchChatResponse = async (searchQuery, signal) => {
  if (!API_URL) {
    Alert.alert("Error", "API URL is not configured.");
    console.error("API URL is not configured.");
    return null;
  }

  try {
    // Build correct GET request URL with question param
    const url = `${API_URL}/api/v1/testq?question=${encodeURIComponent(
      searchQuery
    )}`;

    console.log("Calling API:", url);

    const response = await fetch(url, {
      method: "GET",
      headers: {
        Accept: "application/json",
      },
      signal, // support for abort controller
    });

    if (!response.ok) {
      let errorData = null;

      try {
        errorData = await response.json();
      } catch (e) {
        // Ignore if body isn't JSON
      }

      console.error("Chat API response error:", response.status, errorData);

      throw new Error(
        `HTTP error! status: ${response.status}, message: ${
          errorData?.message || response.statusText
        }`
      );
    }

    // Success
    const data = await response.json();
    console.log("API Response:", data);

    // Return formatted result with follow-up questions included
    return {
      answer: data.answer || "",
      citations: data.citations || [],
      followup_questions: data.followup_questions || [],
      cost: data.cost || 0,
      tokens: data.tokens || 0,
      timestamp: data.timestamp || "",
      question: data.question || searchQuery,
    };
  } catch (error) {
    if (error.name === "AbortError") {
      return null; // user cancelled query
    }

    console.error("Fetch chat response error:", error);
    Alert.alert("Error", `Failed to get chat response: ${error.message}`);

    return null;
  }
};
