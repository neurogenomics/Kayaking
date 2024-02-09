import { Route } from '../src/models/routeModel';
import { Text, TouchableOpacity, View } from 'react-native';
import { Modalize } from 'react-native-modalize';
import React, { useState } from 'react';

type RoutesSlideUpProps = {
  navigation;
  route: {
    params: { routes: Route[] };
  };
};

export const RoutesSlideUp: React.FC<RoutesSlideUpProps> = ({navigation, route}) => {
  const [selectedRoute, setSelectedRoute] = useState<Route>();
  const [modalVisible, setModalVisible] = useState(false);

  return (
    <View>
      <TouchableOpacity onPress={() => setModalVisible(true)}>
        <Text>Show Routes</Text>
      </TouchableOpacity>
      <Modalize
        isOpen={modalVisible}
        onClosed={() => setModalVisible(false)}
        adjustToContentHeight
      >
        {route.params.routes ? (
          route.params.routes.map((route, index) => (
            <TouchableOpacity
              key={index}
              onPress={() => setSelectedRoute(route)}
            >
              <Text>{`Route ${index + 1}: ${Math.round(route.length / 1000)}km`}</Text>
            </TouchableOpacity>
          ))
        ) : (
          <View />
        )}
      </Modalize>
    </View>
  );
};
