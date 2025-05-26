import React, { useState } from 'react';
import { View, Text, Image, TouchableOpacity, StyleSheet } from 'react-native';

export default function Topbar({ user, isAuthenticated, onLogout, onNavigate }) {
  const [showMenu, setShowMenu] = useState(false);

  return (
    <View style={styles.container}>
      <Image
        source={require('../../assets/images/DeepMedIQ-long.jpeg')}
        style={styles.logo}
        resizeMode="contain"
      />
      {!isAuthenticated ? (
        <View style={styles.authButtons}>
          <TouchableOpacity onPress={() => onNavigate('login')} style={styles.loginBtn}>
            <Text style={styles.btnText}>Log in</Text>
          </TouchableOpacity>
          <TouchableOpacity onPress={() => onNavigate('signup')} style={styles.signupBtn}>
            <Text style={styles.btnText}>Sign up</Text>
          </TouchableOpacity>
        </View>
      ) : (
        <View style={styles.profileContainer}>
          <TouchableOpacity onPress={() => setShowMenu(!showMenu)} style={styles.profileBtn}>
            <Image source={{ uri: user.picture }} style={styles.profilePic} />
          </TouchableOpacity>
          {showMenu && (
            <View style={styles.dropdown}>
              <Text>{user.name}</Text>
              <TouchableOpacity onPress={onLogout}>
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
    flexDirection: 'row',
    padding: 12,
    alignItems: 'center',
    justifyContent: 'space-between',
    backgroundColor: '#fff',
  },
  logo: {
    width: 180,
    height: 50,
  },
  authButtons: {
    flexDirection: 'row',
    gap: 12,
  },
  loginBtn: {
    backgroundColor: '#ccc',
    padding: 10,
    borderRadius: 5,
    marginRight: 10,
  },
  signupBtn: {
    backgroundColor: '#007AFF',
    padding: 10,
    borderRadius: 5,
  },
  btnText: {
    color: '#fff',
  },
  profileContainer: {
    position: 'relative',
  },
  profileBtn: {
    borderRadius: 20,
    overflow: 'hidden',
  },
  profilePic: {
    width: 40,
    height: 40,
    borderRadius: 20,
  },
  dropdown: {
    position: 'absolute',
    top: 50,
    right: 0,
    backgroundColor: '#f2f2f2',
    padding: 10,
    borderRadius: 5,
    zIndex: 10,
  },
  logout: {
    color: 'red',
    marginTop: 5,
  },
});
