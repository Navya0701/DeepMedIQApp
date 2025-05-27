import React, { useState, useEffect } from "react";
import { View, StyleSheet, Image } from "react-native";
import SearchBar from "../SearchBar/SearchBar";

export default function Home() {
  const [showSplash, setShowSplash] = useState(true);

  useEffect(() => {
    // Simulate splash screen for 1.5 seconds
    const timer = setTimeout(() => setShowSplash(false), 1500);
    return () => clearTimeout(timer);
  }, []);

  if (showSplash) {
    return (
      <View style={styles.splashContainer}>
        <Image
          source={require("../../assets/images/DeepMedIQ-small.jpeg")}
          style={styles.splashLogo}
          resizeMode="contain"
        />
      </View>
    );
  }

  return (
    <View style={styles.container}>
      <SearchBar />
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: "#fff",
  },
  splashContainer: {
    flex: 1,
    backgroundColor: "#fff",
    justifyContent: "center",
    alignItems: "center",
  },
  splashLogo: {
    width: 140,
    height: 140,
  },
  contentRow: {
    flex: 1,
    flexDirection: "row",
  },
  scrollContent: {
    padding: 12,
    flexGrow: 1,
  },
});
