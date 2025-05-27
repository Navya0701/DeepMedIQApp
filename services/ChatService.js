import Constants from "expo-constants";
import { Alert } from "react-native";

const API_URL = Constants.expoConfig?.extra?.EXPO_PUBLIC_API_URL;

export const fetchChatResponse = async (searchQuery) => {
  if (!API_URL) {
    Alert.alert("Error", "API URL is not configured.");
    console.error("API URL is not configured.");
    return null;
  }
  try {
    const response = await fetch(
      `${API_URL}/chat?message=${encodeURIComponent(searchQuery)}`,
      {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          Accept: "application/json",
        },
        body: JSON.stringify({ message: searchQuery }), // Ensure body is correctly stringified
      }
    );

    if (!response.ok) {
      // Attempt to get error message from response body
      let errorData = null;
      try {
        errorData = await response.json();
      } catch (e) {
        // Ignore if response is not JSON
      }
      console.error("Chat API response error:", response.status, errorData);
      throw new Error(
        `HTTP error! status: ${response.status}, message: ${errorData?.message || response.statusText}`
      );
    }

    const data = await response.json();
    // Remove all logic that replaces **text** with <B>text</B> or any other formatting
    // Just return the data as is
    return data;
  } catch (error) {
    console.error("Fetch chat response error:", error);
    Alert.alert("Error", `Failed to get chat response: ${error.message}`);
    return null; // Or throw error to be caught by caller
  }
};
