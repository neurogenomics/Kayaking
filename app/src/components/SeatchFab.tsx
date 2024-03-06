import { FAB } from 'react-native-paper';
import React from 'react';
import { StyleSheet, View } from 'react-native';
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
    height: 56, // Set a fixed height for the FAB
  },
});

type SearchFabProps = {
  onSearch: () => void;
};

const SearchFab: React.FC<SearchFabProps> = ({ onSearch }: SearchFabProps) => {
  return (
    <FAB
      style={styles.fab}
      icon={() => <FontAwesomeIcon icon={faMagnifyingGlass} />}
      onPress={onSearch}
    />
  );
};

export default SearchFab;
