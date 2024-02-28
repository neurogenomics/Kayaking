const createExpoWebpackConfigAsync = require('@expo/webpack-config');

module.exports = async function (env, argv) {
  const config = await createExpoWebpackConfigAsync(env, argv);
  console.log('isDevServer: ', env.devServer)
  config.resolve.alias['react-native-maps'] = '@teovilla/react-native-web-maps';

  return config;
};