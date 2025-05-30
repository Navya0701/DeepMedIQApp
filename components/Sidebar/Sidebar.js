import React from "react";
import {
  View,
  Text,
  TouchableOpacity,
  ScrollView,
  StyleSheet,
  Image,
  Animated,
  Dimensions,
  Platform,
  Easing,
} from "react-native";
import { MaterialIcons } from "@expo/vector-icons";

const Sidebar = ({
  sessions = [],
  selectedSessionId,
  onSessionSelect,
  onNewSession,
  onSessionDelete,
  onClearAllSessions,
  isVisible,
  onClose,
}) => {
  const { width } = Dimensions.get("window");
  
  // Animation and flicker-free rendering for sidebar
  const slideAnim = React.useRef(
    new Animated.Value(isVisible ? 0 : -width)
  ).current;
  const [shouldRender, setShouldRender] = React.useState(isVisible);

  React.useEffect(() => {
    Animated.timing(slideAnim, {
      toValue: isVisible ? 0 : -width,
      duration: 220,
      useNativeDriver: true,
      easing: Easing.inOut(Easing.cubic),
    }).start();
  }, [isVisible, width]);

  React.useEffect(() => {
    if (isVisible) setShouldRender(true);
    else {
      const timeout = setTimeout(() => setShouldRender(false), 220);
      return () => clearTimeout(timeout);
    }
  }, [isVisible]);
  
  if (!shouldRender) return null;

  return (
    <View style={styles.sidebarContainer}>
      <Animated.View
        style={[
          styles.sidebar,
          { transform: [{ translateX: slideAnim }] },
          {
            shadowOpacity: 0.18,
            shadowRadius: 8,
            shadowColor: "#000",
            elevation: 8,
          },
        ]}
      >
        <View style={styles.mainContainer}>
          <View style={styles.header}>
            <TouchableOpacity
              onPress={onNewSession}
              style={{ marginRight: 8 }}
              accessibilityLabel="New conversation"
            >
              <Image
                source={require("../../assets/images/DeepMedIQ-long.jpeg")}
                style={styles.logo}
                resizeMode="contain"
              />
            </TouchableOpacity>
            <View style={{ flexDirection: "row", alignItems: "center" }}>
              <TouchableOpacity
                onPress={onClearAllSessions}
                style={styles.clearAllButton}
                accessibilityLabel="Clear all history"
              >
                <MaterialIcons name="delete-sweep" size={28} color="#fff" />
              </TouchableOpacity>
              <TouchableOpacity onPress={onClose} style={styles.closeButton}>
                <Text style={styles.closeIcon}>✕</Text>
              </TouchableOpacity>
            </View>
          </View>

          <View style={styles.historyContainer}>
            <Text style={styles.sectionTitle}>
              Sessions ({sessions.length} items)
            </Text>
            <ScrollView style={styles.historyScroll}>
              {sessions.length > 0 ? (
                sessions.map((session, i) => (
                  <View key={session.id} style={styles.sessionContainer}>
                    <View
                      style={[
                        styles.historyItem,
                        session.id === selectedSessionId && {
                          backgroundColor: "#1976D2",
                        },
                      ]}
                    >
                      <TouchableOpacity
                        style={styles.textContainer}
                        onPress={() => onSessionSelect(session.id)}
                      >
                        <Text style={styles.historyText} numberOfLines={2}>
                          {session.headline || "New Chat"}
                        </Text>
                        <View style={styles.sessionInfoRow}>
                          <Text style={styles.timestampText}>
                            {session.createdAt
                              ? new Date(session.createdAt).toLocaleString([], {
                                  year: "2-digit",
                                  month: "short",
                                  day: "numeric",
                                  hour: "2-digit",
                                  minute: "2-digit",
                                })
                              : ""}
                          </Text>
                          {session.qaHistory && session.qaHistory.length > 0 && (
                            <Text style={styles.questionCountText}>
                              {session.qaHistory.length} question{session.qaHistory.length !== 1 ? 's' : ''}
                            </Text>
                          )}
                        </View>
                      </TouchableOpacity>
                      
                      <TouchableOpacity
                        onPress={() => onSessionDelete(session.id)}
                        style={styles.deleteIconContainer}
                      >
                        <MaterialIcons name="delete" size={22} color="#ff6666" />
                      </TouchableOpacity>
                    </View>
                    
                    {session.qaHistory && session.qaHistory.length > 0 && (
                      <View style={styles.questionsContainer}>
                        {session.qaHistory.map((qa, qaIndex) => (
                          <TouchableOpacity
                            key={qa.id || qaIndex}
                            style={styles.questionItem}
                            onPress={() => onSessionSelect(session.id)}
                          >
                            <Text style={styles.questionText} numberOfLines={3}>
                              {qa.question}
                            </Text>
                          </TouchableOpacity>
                        ))}
                      </View>
                    )}
                  </View>
                ))
              ) : (
                <View style={styles.emptyContainer}>
                  <Text style={styles.emptyText}>No sessions yet</Text>
                </View>
              )}
            </ScrollView>
          </View>
        </View>
      </Animated.View>
      <TouchableOpacity
        style={styles.overlay}
        activeOpacity={0.5}
        onPress={onClose}
      />
    </View>
  );
};

const styles = StyleSheet.create({
  sidebarContainer: {
    position: "absolute",
    top: 0,
    left: 0,
    width: "100%",
    height: "100%",
    flexDirection: "row",
    zIndex: 2000,
  },
  sidebar: {
    width: Dimensions.get("window").width * 0.85,
    height: "100%",
    backgroundColor: "transparent",
  },
  mainContainer: {
    flex: 1,
    backgroundColor: "#000",
    padding: 16,
  },
  overlay: {
    flex: 1,
    backgroundColor: "rgba(0, 0, 0, 0.5)",
  },
  header: {
    flexDirection: "row",
    justifyContent: "space-between",
    alignItems: "center",
    marginBottom: 20,
    paddingTop: Platform.OS === "ios" ? 40 : 20,
  },
  logo: {
    width: 180,
    height: 40,
    marginLeft: 8,
    backgroundColor: "#fff",
    borderRadius: 40,
    padding: 8,
  },
  closeButton: {
    padding: 10,
  },
  closeIcon: {
    fontSize: 24,
    color: "#fff",
    fontWeight: "bold",
  },
  historyContainer: {
    flex: 1,
  },
  sectionTitle: {
    fontSize: 18,
    fontWeight: "bold",
    marginBottom: 16,
    color: "#fff",
  },
  historyScroll: {
    flex: 1,
  },
  sessionContainer: {
    marginBottom: 10,
  },
  historyItem: {
    backgroundColor: "#333",
    borderRadius: 8,
    padding: 10,
    flexDirection: "row",
    alignItems: "center",
    shadowColor: "#000",
    shadowOffset: { width: 0, height: 1 },
    shadowOpacity: 0.1,
    shadowRadius: 2,
    elevation: 2,
  },
  textContainer: {
    flex: 1,
  },
  questionsContainer: {
    marginTop: 5,
    marginLeft: 15,
    paddingLeft: 10,
    borderLeftWidth: 2,
    borderLeftColor: "#555",
  },
  questionItem: {
    backgroundColor: "#444",
    borderRadius: 6,
    padding: 8,
    marginBottom: 5,
  },
  questionText: {
    fontSize: 14,
    color: "#ccc",
    lineHeight: 18,
  },
  historyText: {
    fontSize: 16,
    color: "#fff",
    fontWeight: "bold",
    flexWrap: "wrap",
  },
  sessionInfoRow: {
    flexDirection: "row",
    justifyContent: "space-between",
    alignItems: "center",
    marginTop: 2,
  },
  timestampText: {
    fontSize: 12,
    color: "#bbb",
  },
  questionCountText: {
    fontSize: 11,
    color: "#888",
    fontStyle: "italic",
  },
  emptyContainer: {
    padding: 20,
    alignItems: 'center',
    justifyContent: 'center',
  },
  emptyText: {
    fontStyle: "italic",
    color: "#aaa",
    textAlign: "center",
    fontSize: 16,
  },
  deleteIconContainer: {
    marginLeft: 10,
    padding: 4,
  },
  clearAllButton: {
    marginRight: 16,
  },
});

export default Sidebar;