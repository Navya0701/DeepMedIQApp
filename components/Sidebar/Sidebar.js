import React from 'react';
import { 
  View, 
  Text, 
  TouchableOpacity, 
  ScrollView, 
  StyleSheet, 
  Image, 
  Animated, 
  Easing, 
  Platform,
  Dimensions 
} from 'react-native';

const Sidebar = ({ 
  searchHistory = [], 
  onHistoryItemClick,
  isVisible,
  onClose
}) => {  const { width } = Dimensions.get('window');
  const slideAnim = React.useRef(new Animated.Value(-width)).current;
  const [shouldRender, setShouldRender] = React.useState(isVisible);  React.useEffect(() => {
    console.log('Sidebar isVisible changed to:', isVisible);
    if (isVisible) {
      setShouldRender(true);
      Animated.timing(slideAnim, {
        toValue: 0,
        duration: 600, // Slower, smoother opening
        easing: Easing.out(Easing.ease),
        useNativeDriver: true
      }).start();
    } else {
      Animated.timing(slideAnim, {
        toValue: -width,
        duration: 600, // Slower, smoother closing
        easing: Easing.out(Easing.ease), // Use the same easing for closing
        useNativeDriver: true
      }).start(() => {
        setShouldRender(false); // Only hide after animation completes
      });
    }
  }, [isVisible]);

  const truncateQuery = (q, len = 20) => 
    q.length > len ? `${q.substring(0, len)}...` : q;

  if (!shouldRender) return null;

  return (
    <View style={styles.sidebarContainer}>
      <Animated.View 
        style={[
          styles.sidebar,
          { transform: [{ translateX: slideAnim }] }
        ]}
      >
        {/* Main Black Container */}
        <View style={styles.mainContainer}>
          {/* Header with Logo */}
          <View style={styles.header}>
            <Image
              source={require('../../assets/images/DeepMedIQ-long.jpeg')}
              style={styles.logo}
              resizeMode="contain"
            />
            <TouchableOpacity onPress={onClose} style={styles.closeButton}>
              <Text style={styles.closeIcon}>✕</Text>
            </TouchableOpacity>
          </View>

          {/* History Section */}
          <View style={styles.historyContainer}>
            <Text style={styles.sectionTitle}>
              History ({searchHistory.length} items)
            </Text>
            
            <ScrollView style={styles.historyScroll}>
              {searchHistory.length > 0 ? (
                searchHistory.map((query, i) => (
                  <TouchableOpacity
                    key={i}
                    style={styles.historyItem}
                    onPress={() => {
                      onHistoryItemClick(query);
                      onClose();
                    }}
                  >
                    <Text style={styles.historyText}>
                      {truncateQuery(query)}
                    </Text>
                  </TouchableOpacity>
                ))
              ) : (
                <Text style={styles.emptyText}>No search history yet</Text>
              )}
            </ScrollView>
          </View>
        </View>
      </Animated.View>
      
      {/* Overlay */}
      <TouchableOpacity
        style={styles.overlay}
        activeOpacity={1}
        onPress={onClose}
      />
    </View>
  );
};

const styles = StyleSheet.create({
  sidebarContainer: {
    position: 'absolute',
    top: 0,
    left: 0,
    width: '100%',
    height: '100%',
    flexDirection: 'row',
    zIndex: 2000,
  },
  sidebar: {
    width: Dimensions.get('window').width * 0.85,
    height: '100%',
    backgroundColor: 'transparent',
    padding: 8,
  },
  mainContainer: {
    flex: 1,
    backgroundColor: '#000',
    borderRadius: 12,
    padding: 12,
  },  overlay: {
    flex: 1,
    backgroundColor: 'rgba(0, 0, 0, 0.3)', // Changed from transparent to semi-transparent
  },
  header: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    marginBottom: 16,
    paddingTop: Platform.OS === 'ios' ? 40 : 16,
  },
  logo: {
    width: 180,
    height: 40,
    marginLeft: 16,
    marginRight: 16,
    paddingVertical: 8,
    backgroundColor: '#fff',
    borderRadius: 40,
  },
  closeButton: {
    padding: 8,
  },
  closeIcon: {
    fontSize: 24,
    color: '#fff',
    fontWeight: 'bold',
  },
  historyContainer: {
    flex: 1,
    backgroundColor: '#000',
    borderRadius: 8,
    padding: 12,
  },
  sectionTitle: {
    fontSize: 16,
    fontWeight: 'bold',
    marginBottom: 16,
    color: '#fff',
  },
  historyScroll: {
    flex: 1,
  },
  historyItem: {
    paddingVertical: 12,
    borderBottomWidth: 1,
    borderBottomColor: '#333',
  },
  historyText: {
    fontSize: 14,
    color: '#fff',
  },
  emptyText: {
    fontStyle: 'italic',
    color: '#aaa',
    textAlign: 'center',
    marginTop: 20,
  },
});

export default Sidebar;