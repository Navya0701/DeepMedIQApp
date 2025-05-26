import React from 'react';
import { 
  View, 
  Text, 
  TouchableOpacity, 
  ScrollView, 
  StyleSheet, 
  Image, 
  Animated, 
  Dimensions ,
  Platform,
  Easing
} from 'react-native';

const Sidebar = ({ 
  searchHistory = [], 
  onHistoryItemClick,
  isVisible,
  onClose
}) => {
  const { width } = Dimensions.get('window');
  const slideAnim = React.useRef(new Animated.Value(-width)).current;

  React.useEffect(() => {
    Animated.timing(slideAnim, {
      toValue: isVisible ? 0 : -width,
      duration: 300,
      useNativeDriver: true,
      easing: Easing.out(Easing.ease),
    }).start();
  }, [isVisible, width]);

  if (!isVisible) return null;

  const truncateQuery = (q, len = 20) => 
    q.length > len ? `${q.substring(0, len)}...` : q;

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
        activeOpacity={0.5}
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
  },
  mainContainer: {
    flex: 1,
    backgroundColor: '#000',
    padding: 16,
  },
  overlay: {
    flex: 1,
    backgroundColor: 'rgba(0, 0, 0, 0.5)',
  },
  header: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    marginBottom: 20,
    paddingTop: Platform.OS === 'ios' ? 40 : 20,
  },
  logo: {
    width: 180,
    height: 40,
    marginLeft: 8,
    backgroundColor: '#fff',
    borderRadius: 40,
    padding: 8,
  },
  closeButton: {
    padding: 10,
  },
  closeIcon: {
    fontSize: 24,
    color: '#fff',
    fontWeight: 'bold',
  },
  historyContainer: {
    flex: 1,
  },
  sectionTitle: {
    fontSize: 18,
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
    fontSize: 16,
    color: '#fff',
  },
  emptyText: {
    fontStyle: 'italic',
    color: '#aaa',
    textAlign: 'center',
    marginTop: 20,
    fontSize: 16,
  },
});

export default Sidebar;