import * as React from 'react';
import { View, Pressable, Dimensions } from 'react-native';
import Animated, {
  Extrapolate,
  interpolate,
  interpolateColor,
  useAnimatedStyle,
  useSharedValue,
  withTiming,
} from 'react-native-reanimated';
import type { ICarouselInstance } from 'react-native-reanimated-carousel';
import Carousel from 'react-native-reanimated-carousel';

const PAGE_WIDTH = 100;
const PAGE_HEIGHT = 40;

function Index() {
  const r = React.useRef<ICarouselInstance>(null);
  const window = Dimensions.get('window');

  function getNext20Hours(): string[] {
    const result: string[] = [];
    const now = new Date();

    for (let i = 1; i <= 20; i++) {
      const nextHour = new Date(now.getTime() + i * 3600 * 1000);
      const hour = String(nextHour.getHours()).padStart(2, '0');
      const minutes = String(nextHour.getMinutes()).padStart(2, '0');
      result.push(`${hour}:${minutes}`);
    }

    return result;
  }

  const DATA = getNext20Hours(); //['周一', '周二', '周三', '周四', '周五', '周六', '周日'];

  return (
    <View style={{ flex: 1 }}>
      <View>
        <Carousel
          ref={r}
          style={{
            width: window.width,
            height: PAGE_HEIGHT,
            justifyContent: 'center',
            alignItems: 'center',
          }}
          width={window.width / 3}
          height={PAGE_HEIGHT}
          data={DATA}
          renderItem={({ item, animationValue }) => {
            return (
              <Item
                animationValue={animationValue}
                label={item}
                onPress={() =>
                  r.current?.scrollTo({
                    count: animationValue.value,
                    animated: true,
                  })
                }
              />
            );
          }}
        />
      </View>
    </View>
  );
}

export default Index;

interface Props {
  animationValue: Animated.SharedValue<number>;
  label: string;
  onPress?: () => void;
}

const Item: React.FC<Props> = (props) => {
  const { animationValue, label, onPress } = props;

  const translateY = useSharedValue(0);

  const containerStyle = useAnimatedStyle(() => {
    const opacity = interpolate(
      animationValue.value,
      [-1, 0, 1],
      [0.5, 1, 0.5],
      Extrapolate.CLAMP,
    );

    return {
      opacity,
    };
  }, [animationValue]);

  const labelStyle = useAnimatedStyle(() => {
    const scale = interpolate(
      animationValue.value,
      [-1, 0, 1],
      [1, 1.25, 1],
      Extrapolate.CLAMP,
    );

    const color = interpolateColor(
      animationValue.value,
      [-1, 0, 1],
      ['#b6bbc0', '#0071fa', '#b6bbc0'],
    );

    return {
      transform: [{ scale }, { translateY: translateY.value }],
      color,
    };
  }, [animationValue, translateY]);

  const onPressIn = React.useCallback(() => {
    translateY.value = withTiming(-8, { duration: 250 });
  }, [translateY]);

  const onPressOut = React.useCallback(() => {
    translateY.value = withTiming(0, { duration: 250 });
  }, [translateY]);

  return (
    <Pressable onPress={onPress} onPressIn={onPressIn} onPressOut={onPressOut}>
      <Animated.View
        style={[
          {
            height: '100%',
            alignItems: 'center',
            justifyContent: 'center',
          },
          containerStyle,
        ]}
      >
        <Animated.Text style={[{ fontSize: 18, color: '#26292E' }, labelStyle]}>
          {label}
        </Animated.Text>
      </Animated.View>
    </Pressable>
  );
};
