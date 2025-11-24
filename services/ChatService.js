import Constants from "expo-constants";
import { Alert } from "react-native";

const API_URL = process.env.EXPO_PUBLIC_API_URL;
const API_URL2 = process.env.EXPO_PUBLIC_API_URL2;
const API_URL3 = process.env.EXPO_PUBLIC_API_URL3;

export const fetchChatResponse = async (searchQuery, signal) => {
  if (!API_URL) {
    Alert.alert("Error", "API URL is not configured.");
    console.error("API URL is not configured.");
    return null;
  }

  try {
    // Debug: log which API URL we're using
    console.log("ChatService: using API_URL=", API_URL);
    // Build correct GET request URL with question param
    const url = `${API_URL}/api/query?question=${encodeURIComponent(
      searchQuery
    )}`;
    const url_deepthinking = `${API_URL2}/api/v1/testq?question=${encodeURIComponent(
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

    // Success - attempt to parse JSON but handle non-JSON (HTML) responses gracefully
    const contentType = response.headers.get("content-type") || "";
    let data;
    if (contentType.includes("application/json") || contentType.includes("text/json")) {
      data = await response.json();
    } else {
      // Non-JSON response (often an HTML error page). Capture the text for debugging.
      const textBody = await response.text();
      console.error("ChatService: expected JSON but received:", textBody.substring(0, 200));
      // Surface a clearer error to the app
      throw new Error("Server returned non-JSON response. Check the API endpoint or logs.");
    }
    console.log("ChatService: raw API response:", data);

    // Normalize answer from multiple possible keys
    const normalizedAnswer =
      data?.answer || data?.response || data?.text || data?.output || "";

    // Normalize follow-ups from multiple possible keys and shapes
    const rawFollowups =
      data?.followup_questions || data?.followupQuestions || data?.followups || data?.suggested_questions || [];

    // Ensure followups is an array of strings or objects
    const normalizedFollowups = Array.isArray(rawFollowups)
      ? rawFollowups
      : typeof rawFollowups === "string"
      ? [rawFollowups]
      : [];

    return {
      // primary normalized fields (camelCase)
      answer: normalizedAnswer,
      citations: data?.citations || data?.sources || [],
      followupQuestions: normalizedFollowups,
      // keep snake_case for backward compatibility with existing UI
      followup_questions: normalizedFollowups,
      cost: data?.cost || 0,
      tokens: data?.tokens || 0,
      timestamp: data?.timestamp || "",
      question: data?.question || searchQuery,
      // raw: keep original payload for debugging
      raw: data,
      status: response.status,
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
