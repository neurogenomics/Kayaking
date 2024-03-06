import { ActivityIndicator, FAB } from 'react-native-paper';
import React, { useState } from 'react';
import { StyleSheet, View, Text } from 'react-native';
import { COLORS } from '../colors';
import { FontAwesomeIcon } from '@fortawesome/react-native-fontawesome';
import { faMagnifyingGlass } from '@fortawesome/free-solid-svg-icons';

const styles = StyleSheet.create({
  container: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
  },
  fab: {
    position: 'absolute',
    margin: 16,
    right: 0,
    top: 0,
    width: 56, // Set a fixed width for the FAB
    justifyContent: 'center',
    alignItems: 'center',
    alignSelf: 'center', // Align the FAB in the center horizontally
    height: 56, // Set a fixed height for the FAB
  },
});

type SearchFabProps = {
  onSearch: () => Promise<void>;
};

const SearchFab: React.FC<SearchFabProps> = ({ onSearch }: SearchFabProps) => {
  const [isSearching, setIsSearching] = useState(false);

  const handleSearch = () => {
    console.log(isSearching);
    if (isSearching) {
      return;
    }
    setIsSearching(true);
    void onSearch().finally(() => setIsSearching(false));
  };

  return (
    <FAB
      style={styles.fab}
      icon={isSearching ? () => <ActivityIndicator /> : 'magnify'}
      onPress={handleSearch}
    />
  );
};

export default SearchFab;
