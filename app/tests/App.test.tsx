import { render } from '@testing-library/react-native';
import App from '../App';

describe('<App />', () => {
  // Example test
  test('displays a welcome message', () => {
    const view = render(<App />);
    expect(view.getByText('Welcome to the Kayak App')).toBeOnTheScreen();
  });
});
