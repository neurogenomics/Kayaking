import * as React from 'react';
import { View, Dimensions, Pressable } from 'react-native';
import Animated, {
  Extrapolate,
  interpolate,
  useAnimatedStyle,
} from 'react-native-reanimated';
import Carousel, { ICarouselInstance } from 'react-native-reanimated-carousel';
import { format, isSameHour, isToday } from 'date-fns';

type DateCarosoulProps = {
  dates: Date[];
  onDateChanged: (date: Date) => void;
};
const DateCarosoul: React.FC<DateCarosoulProps> = ({
  dates,
  onDateChanged,
}: DateCarosoulProps) => {
  const r = React.useRef<ICarouselInstance>(null);
  const width = Dimensions.get('window').width;
  return (
    <View style={{ flexGrow: 1, paddingTop: 0 }}>
      <Carousel
        ref={r}
        loop={false}
        width={width / 3}
        height={60}
        style={{
          width: width,
          justifyContent: 'center',
          alignItems: 'center',
        }}
        data={dates}
        onSnapToItem={(index) => onDateChanged(dates[index])}
        renderItem={({ item, animationValue }) => (
          <Item
            animationValue={animationValue}
            date={item}
            onPress={() =>
              r.current?.scrollTo({
                count: animationValue.value,
                animated: true,
              })
            }
          ></Item>
        )}
      />
    </View>
  );
};

type ItemProps = {
  animationValue: Animated.SharedValue<number>;
  date: Date;
  onPress?: () => void;
};

const Item: React.FC<ItemProps> = (props) => {
  const { animationValue, date, onPress } = props;

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
        {isNow ? (
          <Animated.Text
            style={[
              { fontSize: 22, color: '#fafafa', fontWeight: 'bold' },
              labelStyle,
            ]}
          >
            {'Now'}
          </Animated.Text>
        ) : (
          <View
            style={[
              {
                height: '100%',
                alignItems: 'center',
                justifyContent: 'center',
              },
            ]}
          >
            <Animated.Text
              style={[
                { fontSize: 18, color: '#fafafa', fontWeight: 'bold' },
                labelStyle,
              ]}
            >
              {timeLabel}
            </Animated.Text>
            <Animated.Text
              style={[
                { fontSize: 10, color: '#fafafa', fontWeight: 'bold' },
                labelStyle,
              ]}
            >
              {dateLabel}
            </Animated.Text>
          </View>
        )}
      </Animated.View>
    </Pressable>
  );
};

export default DateCarosoul;
