import React, { useState } from "react";
import { View, Text, Image, TouchableOpacity, StyleSheet } from "react-native";
import * as AuthSession from "expo-auth-session";

export default function Topbar() {
  const [isAuthenticated, setIsAuthenticated] = useState(false);
  const [user, setUser] = useState(null);
  const [showMenu, setShowMenu] = useState(false);

  // Dummy Auth0 config (replace with your real Auth0 domain/clientId)
  const auth0Domain = "https://dev-xyz123.us.auth0.com";
  const clientId = "YOUR_AUTH0_CLIENT_ID";

  // Google/Apple login handlers (simulate Auth0 Universal Login)
  const handleLogin = async (provider) => {
    // This is a static demo: in real app, use Auth0 Universal Login
    // For demo, just set a fake user
    setUser({
      name: "John Doe",
      picture: "https://i.pravatar.cc/100",
    });
    setIsAuthenticated(true);
  };
  const handleLogout = () => {
    setUser(null);
    setIsAuthenticated(false);
    setShowMenu(false);
  };

  return (
    <View style={styles.container}>
      <Image
        source={require("../../assets/images/DeepMedIQ-long.jpeg")}
        style={styles.logo}
        resizeMode="contain"
      />
      {!isAuthenticated ? (
        <View style={styles.authButtons}>
          <TouchableOpacity
            onPress={() => handleLogin("google")}
            style={styles.loginBtn}
          >
            <Text style={styles.btnText}>Log in</Text>
          </TouchableOpacity>
          <TouchableOpacity
            onPress={() => handleLogin("apple")}
            style={styles.signupBtn}
          >
            <Text style={styles.btnText}>Sign up</Text>
          </TouchableOpacity>
        </View>
      ) : (
        <View style={styles.profileContainer}>
          <TouchableOpacity
            onPress={() => setShowMenu(!showMenu)}
            style={styles.profileBtn}
          >
            <Image source={{ uri: user.picture }} style={styles.profilePic} />
          </TouchableOpacity>
          {showMenu && (
            <View style={styles.dropdown}>
              <Text>{user.name}</Text>
              <TouchableOpacity onPress={handleLogout}>
                <Text style={styles.logout}>Logout</Text>
              </TouchableOpacity>
            </View>
          )}
        </View>
      )}
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flexDirection: "row",
    padding: 12,
    alignItems: "center",
    justifyContent: "space-between",
    backgroundColor: "#fff",
  },
  logo: {
    width: 180,
    height: 50,
  },
  authButtons: {
    flexDirection: "row",
    gap: 8,
  },
  loginBtn: {
    backgroundColor: "#e0e0e0",
    paddingVertical: 6,
    paddingHorizontal: 16,
    borderRadius: 6,
    marginRight: 6,
  },
  signupBtn: {
    backgroundColor: "#e0e0e0",
    paddingVertical: 6,
    paddingHorizontal: 16,
    borderRadius: 6,
  },
  btnText: {
    color: "#333",
    fontWeight: "bold",
    fontSize: 15,
  },
  profileContainer: {
    position: "relative",
  },
  profileBtn: {
    borderRadius: 20,
    overflow: "hidden",
    borderWidth: 2,
    borderColor: "#ccc",
    width: 40,
    height: 40,
    justifyContent: "center",
    alignItems: "center",
    backgroundColor: "#eee",
  },
  profilePic: {
    width: 36,
    height: 36,
    borderRadius: 18,
  },
  dropdown: {
    position: "absolute",
    top: 50,
    right: 0,
    backgroundColor: "#f2f2f2",
    padding: 10,
    borderRadius: 5,
    zIndex: 10,
  },
  logout: {
    color: "red",
    marginTop: 5,
  },
});
