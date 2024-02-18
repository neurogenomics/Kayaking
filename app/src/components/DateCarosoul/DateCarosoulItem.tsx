import React from 'react';
import { View, Pressable, StyleSheet } from 'react-native';
import Animated, {
  Extrapolate,
  interpolate,
  useAnimatedStyle,
} from 'react-native-reanimated';
import { format, isSameHour, isToday } from 'date-fns';

const styles = StyleSheet.create({
  container: {
    height: '100%',
    alignItems: 'center',
    justifyContent: 'center',
  },
  labelStyle: {
    color: '#fafafa',
    fontWeight: 'bold',
  },
});

type DateCarosoulItemProps = {
  animationValue: Animated.SharedValue<number>;
  date: Date;
  onPress?: () => void;
};

const DateCarosoulItem: React.FC<DateCarosoulItemProps> = (props) => {
  const { animationValue, date, onPress } = props;

  const animatedContainerStyle = useAnimatedStyle(() => {
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

  const animatedLabelStyle = useAnimatedStyle(() => {
    const scale = interpolate(
      animationValue.value,
      [-1, 0, 1],
      [1, 1.25, 1],
      Extrapolate.CLAMP,
    );

    return {
      transform: [{ scale }],
    };
  }, [animationValue]);
  const now = new Date();
  const isNow = isSameHour(now, date) && isToday(date);
  const timeLabel = format(date, 'HH:mm');
  const dateLabel = isToday(date) ? 'Today' : format(date, 'do MMM');

  return (
    <Pressable onPress={onPress}>
      <Animated.View style={[styles.container, animatedContainerStyle]}>
        {isNow ? (
          <Animated.Text
            style={[{ fontSize: 22 }, styles.labelStyle, animatedLabelStyle]}
          >
            {'Now'}
          </Animated.Text>
        ) : (
          <View style={styles.container}>
            <Animated.Text
              style={[{ fontSize: 18 }, styles.labelStyle, animatedLabelStyle]}
            >
              {timeLabel}
            </Animated.Text>
            <Animated.Text
              style={[{ fontSize: 10 }, styles.labelStyle, animatedLabelStyle]}
            >
              {dateLabel}
            </Animated.Text>
          </View>
        )}
      </Animated.View>
    </Pressable>
  );
};

export default DateCarosoulItem;
