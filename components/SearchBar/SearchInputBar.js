import React from 'react';
import {
  View,
  TextInput,
  TouchableOpacity,
  Image,
  StyleSheet,
  Animated,
} from 'react-native';

const SearchInputBar = ({
  query,
  onQueryChange,
  onSearchSubmit,
  onToggleRecording,
  isListening,
  micButtonScale,
  searchButtonScale,
  isKeyboardVisible,
  keyboardHeight,
  isTranscribing, // Added new prop
}) => {
  return (
    <View
      style={[
        styles.searchBarContainer,
        {
          transform: [
            {
              translateY: isKeyboardVisible ? -keyboardHeight : 0,
            },
          ],
        },
      ]}
    >
      <View style={styles.searchBar}>
        <TextInput
          style={styles.searchInput}
          placeholder="How can DeepMedIQ help you today..."
          placeholderTextColor="#889"
          value={query}
          multiline
          onChangeText={onQueryChange}
          onSubmitEditing={onSearchSubmit}
        />
        <Animated.View style={{ transform: [{ scale: micButtonScale }] }}>
          <TouchableOpacity
            style={[styles.micButton, isListening && styles.micButtonActive]}
            onPress={onToggleRecording}
            disabled={isTranscribing} // Disable button while transcribing
          >
            {isTranscribing ? (
              <Image 
                source={require("../../assets/images/loadingimage-2.gif")} // Replace with your loading GIF
                style={styles.loadingIcon}
              />
            ) : (
              <Image
                source={require("../../assets/images/mic.png")}
                style={[styles.micIcon, isListening && styles.micIconActive]}
              />
            )}
            {isListening && !isTranscribing && <View style={styles.recordingIndicator} />}
          </TouchableOpacity>
        </Animated.View>
        <Animated.View style={{ transform: [{ scale: searchButtonScale }] }}>
          <TouchableOpacity
            style={styles.searchButton}
            onPress={onSearchSubmit} // Changed from handleSearch to onSearchSubmit
          >
            <Image
              source={require("../../assets/images/DeepMedIQ-small.jpeg")}
              style={styles.searchButtonImage}
            />
          </TouchableOpacity>
        </Animated.View>
      </View>
    </View>
  );
};

const styles = StyleSheet.create({
  searchBarContainer: {
    position: 'absolute',
    bottom: 0,
    left: 0,
    right: 0,
    backgroundColor: '#fff',
    borderTopWidth: 1,
    borderTopColor: '#eee',
    zIndex: 1000,
  },
  searchBar: {
    flexDirection: 'row',
    alignItems: 'center',
    padding: 12,
    backgroundColor: '#f5f5f5',
  },
  searchInput: {
    flex: 1,
    backgroundColor: '#fff',
    borderRadius: 8,
    padding: 12,
    fontSize: 16,
    borderWidth: 1,
    borderColor: '#ddd',
    maxHeight: 120,
    minHeight: 50,
  },
  micButton: {
    marginLeft: 8,
    padding: 10,
    backgroundColor: '#fff',
    borderRadius: 20,
    elevation: 2,
  },
  micButtonActive: {
    backgroundColor: '#CB2323',
  },
  micIcon: {
    width: 24,
    height: 24,
    tintColor: '#333',
  },
  loadingIcon: { // Style for the loading GIF
    width: 24,
    height: 24,
  },
  micIconActive: {
    tintColor: '#fff',
  },
  recordingIndicator: {
    position: 'absolute',
    top: 8,
    right: 8,
    width: 8,
    height: 8,
    borderRadius: 4,
    backgroundColor: 'red',
  },
  searchButton: {
    marginLeft: 12,
    padding: 8,
    backgroundColor: '#fff',
    borderRadius: 8,
    elevation: 2,
  },
  searchButtonImage: {
    width: 32,
    height: 32,
    borderRadius: 16,
  },
});

export default SearchInputBar;
