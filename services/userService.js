import AsyncStorage from "@react-native-async-storage/async-storage";

// Generate fallback random ID
function randomId() {
  return "anon_" + Math.random().toString(36).substring(2) + Date.now().toString(36);
}

// Unique persistent anonymous user ID
export async function getOrCreateUserId() {
  try {
    const existingId = await AsyncStorage.getItem("anonUserId");
    if (existingId) {
      return existingId;
    }

    const newId = randomId();
    await AsyncStorage.setItem("anonUserId", newId);

    console.log("🆕 Created anonymous UserID:", newId);
    return newId;

  } catch (error) {
    console.log("⚠ AsyncStorage Error:", error);
    return randomId(); // fallback only
  }
}
