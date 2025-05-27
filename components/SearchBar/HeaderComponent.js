import React from "react";
import { View, Text, TouchableOpacity, Image, StyleSheet } from "react-native";
import * as AuthSession from "expo-auth-session";

const auth0Domain = "dev-lloatlcbli65717r.us.auth0.com";
const auth0ClientId = "7djYd5AsHjB9Qs2BcBxAFbWY38Af9vKP";
const redirectUri = AuthSession.makeRedirectUri({ useProxy: true });

const discovery = {
  authorizationEndpoint: `https://${auth0Domain}/authorize`,
  tokenEndpoint: `https://${auth0Domain}/oauth/token`,
};

const HeaderComponent = ({ onMenuPress }) => {
  const [isAuthenticated, setIsAuthenticated] = React.useState(false);
  const [user, setUser] = React.useState(null);
  const [showMenu, setShowMenu] = React.useState(false);
  const [request, response, promptAsync] = AuthSession.useAuthRequest(
    {
      clientId: auth0ClientId,
      redirectUri,
      scopes: ["openid", "profile", "email"],
      responseType: "token",
      extraParams: {
        audience: `https://${auth0Domain}/userinfo`,
        // Remove connection param so Auth0 shows all enabled social providers (Apple, Microsoft, etc.)
      },
    },
    discovery
  );

  React.useEffect(() => {
    if (response?.type === "success") {
      const { access_token } = response.params;
      fetch(`https://${auth0Domain}/userinfo`, {
        headers: { Authorization: `Bearer ${access_token}` },
      })
        .then((res) => res.json())
        .then((data) => {
          setUser(data);
          setIsAuthenticated(true);
        });
    }
  }, [response]);

  const handleLogin = async () => {
    await promptAsync({
      useProxy: true,
      // No connection param, shows all providers
    });
  };

  const handleSignup = async () => {
    await promptAsync({
      useProxy: true,
      extraParams: {
        screen_hint: "signup",
        // No connection param, shows all providers
      },
    });
  };

  const handleLogout = () => {
    setUser(null);
    setIsAuthenticated(false);
    setShowMenu(false);
  };

  return (
    <View style={styles.header}>
      <TouchableOpacity onPress={onMenuPress} style={styles.menuButton}>
        <Text style={styles.menuIcon}>☰</Text>
      </TouchableOpacity>
      <View style={styles.logoContainer}>
        <Image
          source={require("../../assets/images/DeepMedIQ-long.jpeg")}
          style={styles.headerLogo}
          resizeMode="contain"
        />
      </View>
      {!isAuthenticated ? (
        <View style={styles.authButtons}>
          <TouchableOpacity onPress={handleLogin} style={styles.loginBtn}>
            <Text style={styles.btnText}>Log in</Text>
          </TouchableOpacity>
          <TouchableOpacity onPress={handleSignup} style={styles.signupBtn}>
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
              <View style={styles.profileDropdownHeader}>
                <Image
                  source={{ uri: user.picture }}
                  style={styles.profilePicLarge}
                />
                <Text style={styles.profileName} numberOfLines={1}>
                  {user.name}
                </Text>
              </View>
              <TouchableOpacity
                onPress={handleLogout}
                style={styles.logoutBtn}
              >
                <Text style={styles.logout}>Logout</Text>
              </TouchableOpacity>
            </View>
          )}
        </View>
      )}
    </View>
  );
};

const styles = StyleSheet.create({
  header: {
    height: 60,
    flexDirection: "row",
    alignItems: "center",
    paddingHorizontal: 16,
    backgroundColor: "#fff",
    borderBottomWidth: 1,
    borderBottomColor: "#eee",
    zIndex: 1000,
    elevation: 5,
    shadowColor: "#000",
    shadowOffset: { width: 0, height: 2 },
    shadowOpacity: 0.1,
    shadowRadius: 3,
  },
  menuButton: {
    padding: 8,
    marginRight: 16,
  },
  menuIcon: {
    fontSize: 24,
    color: "#333",
  },
  logoContainer: {
    flex: 1,
    justifyContent: "center",
    alignItems: "center",
    marginRight: 40, // To compensate for the menu button and keep logo centered
  },
  headerLogo: {
    width: 180,
    height: 40,
  },
  authButtons: {
    flexDirection: "row",
    gap: 8,
    marginLeft: 8,
  },
  loginBtn: {
    backgroundColor: "#e0e0e0",
    paddingVertical: 4,
    paddingHorizontal: 12,
    borderRadius: 6,
    marginRight: 6,
  },
  signupBtn: {
    backgroundColor: "#e0e0e0",
    paddingVertical: 4,
    paddingHorizontal: 12,
    borderRadius: 6,
  },
  btnText: {
    color: "#333",
    fontWeight: "bold",
    fontSize: 13,
  },
  profileContainer: {
    position: "relative",
    marginLeft: 8,
  },
  profileBtn: {
    borderRadius: 20,
    overflow: "hidden",
    borderWidth: 2,
    borderColor: "#ccc",
    width: 36,
    height: 36,
    justifyContent: "center",
    alignItems: "center",
    backgroundColor: "#eee",
  },
  profilePic: {
    width: 32,
    height: 32,
    borderRadius: 16,
  },
  dropdown: {
    position: "absolute",
    top: 40,
    right: 0,
    backgroundColor: "#f2f2f2",
    padding: 12,
    borderRadius: 10,
    zIndex: 10,
    minWidth: 160,
    alignItems: "center",
    shadowColor: "#000",
    shadowOffset: { width: 0, height: 2 },
    shadowOpacity: 0.15,
    shadowRadius: 6,
    elevation: 6,
  },
  profileDropdownHeader: {
    alignItems: "center",
    marginBottom: 8,
  },
  profilePicLarge: {
    width: 48,
    height: 48,
    borderRadius: 24,
    marginBottom: 4,
    borderWidth: 2,
    borderColor: "#1976D2",
  },
  profileName: {
    fontWeight: "bold",
    fontSize: 15,
    color: "#222",
    maxWidth: 120,
    textAlign: "center",
  },
  logoutBtn: {
    marginTop: 6,
    backgroundColor: "#fff",
    borderRadius: 6,
    paddingVertical: 6,
    paddingHorizontal: 18,
    width: "100%",
    alignItems: "center",
  },
  logout: {
    color: "#CB2323",
    fontWeight: "bold",
    fontSize: 15,
  },
});

export default HeaderComponent;
