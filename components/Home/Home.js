import React, { useState } from 'react';
import { View, StyleSheet } from 'react-native';
import SearchBar from '../SearchBar/SearchBar';

export default function Home() {
  const [query, setQuery] = useState('');

  const handleSearch = (searchQuery) => {
    if (searchQuery.trim() === '') return;
  };

  return (
    <View style={styles.container}>
      <SearchBar
        // query={query}
        // setQuery={setQuery}
        // handleSearch={handleSearch}
      />
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#fff',
  },
  contentRow: {
    flex: 1,
    flexDirection: 'row',
  },
  scrollContent: {
    padding: 12,
    flexGrow: 1,
  },
});
