import { useState, useEffect } from 'react';
import { View, Text, Button } from 'react-native';

type DisplayResponseProps = {
  url: string;
};

const DisplayResponse = ({ url }: DisplayResponseProps) => {
  const [display, setDisplay] = useState('');
  const [loading, setLoading] = useState(true);

  const fetchData = async () => {
    setLoading(true);
    console.log(url);
    try {
      const response = await fetch(url);
      const jsonStr = await response.text();
      const prettyJsonStr = JSON.stringify(JSON.parse(jsonStr), null, 2);
      setDisplay(prettyJsonStr);
    } catch (error) {
      setDisplay('Error fetching data: ' + error);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    void fetchData();
  }, []);

  return (
    <View>
      {loading ? (
        <Text>Loading...</Text>
      ) : display ? (
        <View>
          <Text>{display}</Text>
        </View>
      ) : (
        <Text>Failed to fetch data</Text>
      )}
      <Button title="Refresh" onPress={() => void fetchData()}></Button>
    </View>
  );
};

export default DisplayResponse;
