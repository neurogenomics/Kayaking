import { useEffect, useState } from 'react';
import { LocationModel } from '../../models/locationModel';
import { SunsetInfo } from '../../models/sunsetModel';
import { TideInfo } from '../../models/tideModel';
import { View, Text, StyleSheet } from 'react-native';
import { getSunset } from '../../services/sunsetService';
import { format } from 'date-fns';

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
  tideInfo?: TideInfo;
  startTime: Date;
  location: LocationModel;
};

export const DataDisplay: React.FC<DataDisplayProps> = ({
  sunsetOn,
  tideTimesOn,
  tideInfo,
  startTime,
  location,
}: DataDisplayProps) => {
  const [sunsetInfo, setSunsetInfo] = useState<SunsetInfo>();
  const getSunInfo = async (location: LocationModel, date: Date) => {
    try {
      const sunInfo: SunsetInfo = await getSunset(location, date);
      setSunsetInfo(sunInfo);
    } catch (error) {
      console.log('Error getting sunset info: ', error);
    }
  };

  const getSunTimeString = (time: Date) => {
    const sunTime = time.toString().match(/^\d{2}:\d{2}/);
    if (sunTime) {
      return sunTime[0];
    }
  };

  useEffect(() => {
    if (sunsetOn) {
      void getSunInfo(location, startTime);
    }
  }, [sunsetOn]);

  return (
    <View style={styles.container}>
      {sunsetOn && sunsetInfo !== undefined ? (
        <View style={styles.infoContainer}>
          <View style={styles.textContainer}>
            <Text style={styles.text}>
              Sunrise Time: {getSunTimeString(sunsetInfo.sunrise)}
            </Text>
          </View>
          <View style={styles.textContainer}>
            <Text style={styles.text}>
              Sunrise Time: {getSunTimeString(sunsetInfo.sunset)}
            </Text>
          </View>
        </View>
      ) : (
        <></>
      )}
      {tideTimesOn ? (
        <View style={styles.infoContainer}>
          <View style={styles.textContainer}>
            {tideInfo &&
              tideInfo.events
                .map(
                  (info, index) =>
                    info.datetime.getTime() - startTime.getTime() > 0 && (
                      <Text key={index} style={styles.text}>
                        {info.isHighTide ? 'High Tide' : 'Low Tide'}
                        {info.height ? ` of ${info.height.toFixed(1)}m` : ''}
                        {info.datetime
                          ? ` at ${format(info.datetime, 'HH:mm')}`
                          : ''}
                        {}
                      </Text>
                    ),
                )
                .filter((item) => item)
                .slice(0, 1)}
            {tideInfo && (
              <Text style={styles.text}>
                Source: {tideInfo.source.name} Tide Station
              </Text>
            )}
          </View>
        </View>
      ) : (
        <></>
      )}
    </View>
  );
};
