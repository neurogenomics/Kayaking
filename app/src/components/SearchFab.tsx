import { ActivityIndicator, FAB } from 'react-native-paper';
import React, { useState } from 'react';
import { StyleSheet } from 'react-native';

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
    justifyContent: 'center',
    alignItems: 'center',
    alignSelf: 'center',
    height: 56,
    width: 56,
  },
});

type SearchFabProps = {
  onSearch: () => Promise<void>;
};

const SearchFab: React.FC<SearchFabProps> = ({ onSearch }: SearchFabProps) => {
  const [isSearching, setIsSearching] = useState(false);

  const handleSearch = () => {
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
