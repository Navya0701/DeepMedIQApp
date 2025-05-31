import React from "react";
import {
  View,
  TextInput,
  TouchableOpacity,
  Image,
  StyleSheet,
  Animated,
  ActivityIndicator,
  Easing,
} from "react-native";
import { Ionicons } from '@expo/vector-icons';

const SearchInputBar = ({
  // related to the search input
  query,
  onQueryChange,
  // these are related toto the keyboard and its visibility
  searchButtonScale,
  isKeyboardVisible,
  keyboardHeight,
  // these below 3 are are for the submit button and the response related
  loading,
  onSearchSubmit,
  onStopResponse,
  // these below 3 are the important props for recording
  startRecording,
  stopRecording,
  recording,
  isTranscribing
}) => {
  const [isFocused, setIsFocused] = React.useState(false);
  // Animated value for keyboard translation
  const translateYAnim = React.useRef(new Animated.Value(0)).current;

  // Animate translateY when keyboard height changes
  React.useEffect(() => {
    Animated.timing(translateYAnim, {
      toValue: isKeyboardVisible ? -keyboardHeight : 0,
      duration: 320,
      easing: Easing.out(Easing.cubic),
      useNativeDriver: true,
    }).start();
  }, [isKeyboardVisible, keyboardHeight]);

  return (
    <Animated.View
      style={[
        styles.searchBarContainer,
        {
          transform: [
            {
              translateY: translateYAnim,
            },
          ],
        },
      ]}
    >
      <View style={styles.searchBarRounded}>
        <View style={styles.inputWithMic}>
          <TextInput
            style={styles.searchInputRounded}
            placeholder="How can DeepMedIQ help you today..."
            placeholderTextColor="#889"
            value={query}
            multiline
            onChangeText={onQueryChange}
            onSubmitEditing={onSearchSubmit}
            onFocus={() => setIsFocused && setIsFocused(true)}
            onBlur={() => setIsFocused && setIsFocused(false)}
            editable={true}
            pointerEvents="auto"
            importantForAccessibility="yes"
            allowFontScaling={true}
            autoFocus={false}
            keyboardType="default"
            returnKeyType="send"
          />
           <TouchableOpacity
              onPress={recording ? stopRecording : startRecording}
              style={{
                backgroundColor: recording ? 'white' : 'white',
                padding: 15,
                marginLeft: 10,
                borderRadius: 50,
              }}>
               {!recording ? (
                <Image source={require("../../assets/images/mic.png")} style={{ width: 20, height: 20 }} />
              ) : (
                <Image source={require("../../assets/images/stop.png")} style={{ width: 20, height: 20 }} />
              )}
          </TouchableOpacity>
        </View>
        <Animated.View style={{ transform: [{ scale: searchButtonScale }] }}>
           <TouchableOpacity
            style={[
              styles.searchButton,
              loading && styles.searchButtonDisabled,
            ]}
            onPress={() => {
              if (loading) {
                if (onStopResponse) onStopResponse();
                return;
              }
              onSearchSubmit();
            }}
            disabled={false}
            activeOpacity={0.7}
            accessible={true}
            accessibilityLabel="Send"
          >
            {loading ? (
              <View style={styles.spinnerStyle}>
                <View style={styles.spinnerCircle} />
              </View>
            ) : (
              <Image
                source={require("../../assets/images/DeepMedIQ-small.jpeg")}
                style={styles.searchButtonImage}
              />
            )}
          </TouchableOpacity>
        </Animated.View>
      </View>
    </Animated.View>
  );
};

const styles = StyleSheet.create({
  searchBarContainer: {
    position: "absolute",
    bottom: 0,
    left: 0,
    right: 0,
    backgroundColor: "#fff",
    borderTopWidth: 1,
    borderTopColor: "#eee",
    zIndex: 1000,
  },
  searchBarRounded: {
    flexDirection: "row",
    alignItems: "center",
    backgroundColor: "#f5f5f5",
    borderRadius: 32,
    margin: 12,
    paddingHorizontal: 12,
    paddingVertical: 6,
    shadowColor: "#1976D2",
    shadowOffset: { width: 0, height: 2 },
    shadowOpacity: 0.1,
    shadowRadius: 8,
    elevation: 4,
  },
  inputWithMic: {
    flex: 1,
    flexDirection: "row",
    alignItems: "center",
  },
  searchInputRounded: {
    flex: 1,
    backgroundColor: "#fff",
    borderRadius: 32,
    paddingVertical: 10,
    paddingHorizontal: 16,
    fontSize: 16,
    borderWidth: 0,
    color: "#222",
  },
  micButtonInline: {
    marginLeft: 6,
    padding: 8,
    backgroundColor: "#fff",
    borderRadius: 20,
    elevation: 2,
    justifyContent: "center",
    alignItems: "center",
  },
  micButtonActive: {
    backgroundColor: "#CB2323",
  },
  micIcon: {
    width: 24,
    height: 24,
    tintColor: "#333",
  },
  loadingIcon: {
    // Style for the loading GIF
    width: 24,
    height: 24,
  },
  micIconActive: {
    tintColor: "#fff",
  },
  recordingIndicator: {
    position: "absolute",
    top: 8,
    right: 8,
    width: 8,
    height: 8,
    borderRadius: 4,
    backgroundColor: "red",
  },
  searchButton: {
    marginLeft: 12,
    padding: 8,
    backgroundColor: "#fff",
    borderRadius: 8,
    elevation: 2,
  },
  searchButtonDisabled: {
    opacity: 0.6,
  },
  searchButtonImage: {
    width: 32,
    height: 32,
    borderRadius: 16,
  },
  searchButtonLoadingIcon: {
    width: 32,
    height: 32,
    borderRadius: 16,
  },
  // Remove styles: loadingGif and loadingGifContainer as they are not used
  // Add spinner style
  spinnerStyle: {
    width: 32,
    height: 32,
    justifyContent: "center",
    alignItems: "center",
  },
  spinnerCircle: {
    width: 22,
    height: 22,
    borderWidth: 3,
    borderColor: "#1976D2",
    borderTopColor: "transparent",
    borderRadius: 11,
    borderStyle: "solid",
    // Simple spin animation
    transform: [{ rotate: "0deg" }],
  },
});

export default SearchInputBar;
