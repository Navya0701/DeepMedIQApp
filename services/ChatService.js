import AsyncStorage from "@react-native-async-storage/async-storage";
import { Alert } from "react-native";
import { getOrCreateUserId } from "./userService";

// Backend URLs
const API_URL = process.env.EXPO_PUBLIC_API_URL;   // Azure → Normal AI
const API_URL2 = process.env.EXPO_PUBLIC_API_URL2; // Google Cloud Run → DeepThink + Firestore

/** Fetch AI Response **/
export const fetchChatResponse = async (searchQuery, signal, isDeepThinkEnabled) => {
  const BASE_URL = isDeepThinkEnabled ? API_URL2 : API_URL;

  if (!BASE_URL) {
    Alert.alert("Config Missing", "Backend URL Not Set");
    return null;
  }

  const endpoint = isDeepThinkEnabled ? "/api/v1/testq" : "/api/query";
  const url = `${BASE_URL}${endpoint}?question=${encodeURIComponent(searchQuery)}`;

  console.log("🌍 Calling:", url);

  try {
    const res = await fetch(url, { method: "GET", signal });

    if (!res.ok) throw new Error(`Server error: ${res.status}`);

    const data = await res.json();
    console.log("🤖 AI Response:", data);

    return {
      answer: data?.answer,
      followup_questions: data?.followup_questions || []
    };
  } catch (err) {
    if (err.name !== "AbortError") {
      Alert.alert("Backend Error", err.message);
    }
    return null;
  }
};


/** 🔥 Create a new thread in Firestore **/
export const createNewThread = async () => {
  const userId = await getOrCreateUserId();
  if (!API_URL2 || !userId) return null;

  try {
    const res = await fetch(`${API_URL2}/threads`, {
      method: "POST",
      headers: {"Content-Type": "application/json"},
      body: JSON.stringify({ userId })
    });

    const data = await res.json();
    const threadId = data?.threadId;
    console.log("🧵 New Thread:", threadId);

    await AsyncStorage.setItem("currentThreadId", threadId);
    return threadId;
  } catch (err) {
    console.error("❌ Thread Create Error:", err);
    return null;
  }
};


/** 💾 Save Message into Firestore **/
export const saveMessage = async (role, content) => {
  const userId = await getOrCreateUserId();
  if (!API_URL2 || !userId) return;

  let threadId = await AsyncStorage.getItem("currentThreadId");

  if (!threadId) {
    console.log("⚠ No thread exists → Creating new...");
    threadId = await createNewThread();
    if (!threadId) return;
  }

  try {
    await fetch(`${API_URL2}/threads/${threadId}/messages`, {
      method: "POST",
      headers: {"Content-Type": "application/json"},
      body: JSON.stringify({ userId, role, content })
    });
    console.log(`💬 Saved: ${role} → ${content}`);
  } catch (err) {
    console.error("Message Save Failed:", err);
  }
};


/** 📌 Load messages for selected thread **/
export const loadThreadMessages = async (threadId) => {
  const userId = await getOrCreateUserId();
  if (!API_URL2 || !userId) return [];

  const url = `${API_URL2}/threads/${threadId}/messages?userId=${userId}`;
  try {
    const res = await fetch(url);
    const data = await res.json();
    console.log("📜 Loaded Thread Messages:", data);
    return data;
  } catch (err) {
    console.error("Failed to get thread messages:", err);
    return [];
  }
};


/** 📌 Load all threads for a user **/
export const loadUserThreads = async () => {
  const userId = await getOrCreateUserId();
  if (!API_URL2 || !userId) return [];

  const url = `${API_URL2}/threads?userId=${userId}`;
  try {
    const res = await fetch(url);
    const data = await res.json();
    console.log("🧵 All Threads:", data);
    return data;
  } catch (err) {
    console.error("Failed to load threads:", err);
    return [];
  }
};
