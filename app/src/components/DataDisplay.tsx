import { useEffect, useState } from 'react';
import { getSunset } from '../services/sunsetService';
import { LocationModel } from '../models/locationModel';
import { SunsetInfo } from '../models/sunsetModel';
import { StyleSheet, View, Text } from 'react-native';
import { TideEvent } from '../models/tideModel';
import { getTideTimes } from '../services/tideTimesService';

export const styles = StyleSheet.create({
  container: {
    flexDirection: 'column',
    alignItems: 'flex-start',
  },
  infoContainer: {
    flexDirection: 'row',
  },
  textContainer: {
    marginTop: 0,
    margin: 10,
  },
  text: {
    fontWeight: 'bold',
  },
});

type DataDisplayProps = {
  sunsetOn: boolean;
  tideTimesOn: boolean;
  location: LocationModel;
  date: Date;
};

export const DataDisplay: React.FC<DataDisplayProps> = ({
  sunsetOn,
  tideTimesOn,
  location,
  date,
}: DataDisplayProps) => {
  const [sunsetInfo, setSunsetInfo] = useState<SunsetInfo>();
  const [nextTideInfo, setNextTideInfo] = useState<TideEvent>();
  const getSunInfo = async (location: LocationModel, date: Date) => {
    try {
      const sunInfo: SunsetInfo = await getSunset(location, date);
      setSunsetInfo(sunInfo);
    } catch (error) {
      console.log('Error getting sunset info: ', error);
    }
  };

  const getNextTideInfo = (events: [TideEvent]): TideEvent => {
    const currentDate = new Date();
    for (let i = 0; i < events.length; i++) {
      // comparison between regular datetime and currentDate was not working as expected
      if (new Date(events[i].datetime.toString()) > currentDate) {
        return events[i];
      }
    }
    return events[0];
  };

  const getTideInfo = async (location: LocationModel) => {
    try {
      await getTideTimes(location).then((tideinfo) =>
        setNextTideInfo(getNextTideInfo(tideinfo.events)),
      );
    } catch (error) {
      console.log('Error getting tide info: ', error);
    }
  };

  const getTimeString = (datetime: Date) => {
    const timeRegex = /T(\d{2}:\d{2})/;
    const extractedTime = datetime.toString().match(timeRegex);
    return extractedTime ? extractedTime[1] : '';
  };

  useEffect(() => {
    if (sunsetOn) {
      void getSunInfo(location, date);
    }
    if (tideTimesOn) {
      // TODO change to actual location once tide times service fixed
      void getTideInfo({ latitude: 50.67, longitude: 1.5 });
    }
  }, [sunsetOn, tideTimesOn]);

  return (
    <View style={styles.container}>
      {sunsetOn && sunsetInfo !== undefined ? (
        <View style={styles.infoContainer}>
          <View style={styles.textContainer}>
            <Text style={styles.text}>
              Sunrise Time: {sunsetInfo.sunrise.toString()}
            </Text>
          </View>
          <View style={styles.textContainer}>
            <Text style={styles.text}>
              Sunrise Time: {sunsetInfo.sunset.toString()}
            </Text>
          </View>
        </View>
      ) : (
        <></>
      )}
      {/*TODO: find out what information is required to be displayed*/}
      {tideTimesOn && nextTideInfo !== undefined ? (
        <View style={styles.infoContainer}>
          <View style={styles.textContainer}>
            <Text style={styles.text}>
              {nextTideInfo.isHighTide ? 'High tide ' : 'Low tide '}
              {nextTideInfo.height
                ? `of ${nextTideInfo.height.toFixed(1)}m `
                : ''}
              {nextTideInfo.datetime
                ? `at ${getTimeString(nextTideInfo.datetime)}`
                : ''}
            </Text>
          </View>
        </View>
      ) : (
        <></>
      )}
    </View>
  );
};
