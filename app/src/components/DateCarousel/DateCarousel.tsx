import React from 'react';
import { View, Dimensions, StyleSheet } from 'react-native';
import Carousel, { ICarouselInstance } from 'react-native-reanimated-carousel';
import DateCarouselItem from './DateCarouselItem';

const styles = StyleSheet.create({
  carousel: {
    width: '100%',
    justifyContent: 'center',
    alignItems: 'center',
  },
});

type DateCarosoulProps = {
  dates: Date[];
  onDateChanged: (date: Date) => void;
};
const DateCarousel: React.FC<DateCarosoulProps> = ({
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
        // There is no support in the library to have this dynamic height but since font-sizes are fixed it shouldn't matter
        height={60}
        style={styles.carousel}
        data={dates}
        onSnapToItem={(index) => onDateChanged(dates[index])}
        renderItem={({ item, animationValue }) => (
          <DateCarouselItem
            animationValue={animationValue}
            date={item}
            // Scroll to item when you click it
            onPress={() =>
              r.current?.scrollTo({
                count: animationValue.value,
                animated: true,
              })
            }
          ></DateCarouselItem>
        )}
      />
    </View>
  );
};

export default DateCarousel;
