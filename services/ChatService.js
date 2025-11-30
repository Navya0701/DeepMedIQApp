import AsyncStorage from "@react-native-async-storage/async-storage";
import { Alert } from "react-native";
import { getOrCreateUserId } from "./userService";

// Backend URLs
const API_URL = process.env.EXPO_PUBLIC_API_URL;  // Azure → AI only
const API_URL2 = process.env.EXPO_PUBLIC_API_URL2; // Google → Firestore only


// 🌟 Fetch AI response (Azure)
export const fetchChatResponse = async (searchQuery, signal) => {

  if (!API_URL) {
    console.error("❌ Missing EXPO_PUBLIC_API_URL");
    Alert.alert("Configuration Error", "AI server URL not set.");
    return null;
  }

  try {
    const queryUrl = `${API_URL}/api/query?question=${encodeURIComponent(searchQuery)}`;
    console.log("🌍 Calling AI:", queryUrl);

    const res = await fetch(queryUrl, {
      method: "GET",
      headers: { Accept: "application/json" },
      signal,
    });

    if (!res.ok) throw new Error(`Server error: ${res.status}`);

    const data = await res.json();
    console.log("🤖 AI Response:", data);

    return {
      answer: data?.answer || "No answer received",
      followup_questions:
        Array.isArray(data?.followup_questions) ? data.followup_questions : [],
      question: searchQuery,
    };

  } catch (err) {
    if (err.name !== "AbortError") {
      console.error("❌ AI Request Failed:", err);
      Alert.alert("Error", err.message);
    }
    return null;
  }
};


// 🔥 Create a new thread (Google Backend)
export async function createNewThread() {
  if (!API_URL2) {
    console.warn("⚠ Firestore backend missing → skipping thread creation");
    return null;
  }

  try {
    const userId = await getOrCreateUserId();

    const res = await fetch(`${API_URL2}/threads`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ userId }),
    });

    const data = await res.json();
    const threadId = data?.threadId;

    if (!threadId) throw new Error("Thread ID missing in response");

    await AsyncStorage.setItem("currentThreadId", threadId);
    console.log("🧵 Thread Created:", threadId);

    return threadId;

  } catch (err) {
    console.error("❌ Thread Creation Failed:", err);
    return null;
  }
}


// 💾 Save message to Firestore (Google Backend)
export async function saveMessage(role, content) {
  if (!API_URL2) {
    console.log("⚠ Firestore disabled → Skipping message save");
    return;
  }

  const userId = await getOrCreateUserId();
  let threadId = await AsyncStorage.getItem("currentThreadId");

  if (!threadId) {
    console.log("⚠ No thread exists → Creating...");
    threadId = await createNewThread();
    if (!threadId) return;
  }

  try {
    await fetch(`${API_URL2}/messages`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ userId, threadId, role, content }),
    });

    console.log(`💾 Message saved (${role}):`, content);

  } catch (err) {
    console.error("❌ Failed to save message:", err);
  }
}
