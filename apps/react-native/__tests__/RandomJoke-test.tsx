import React from 'react'
import renderer from 'react-test-renderer'
import {RandomJokeScreen} from '@presentation/screens'
import {TestApp} from './TestApp'
import type {RandomJokeScreenProps} from '@presentation/types'

test('renders correctly', () => {
  // This test only uses tabPress registration, not the full navigator.
  const navigation = {
    addListener: jest.fn(() => jest.fn()),
  } as unknown as RandomJokeScreenProps['navigation']
  const route: RandomJokeScreenProps['route'] = {
    key: 'random-joke-test',
    name: 'RandomTab',
  }
  const tree = renderer
    .create(
      <TestApp>
        <RandomJokeScreen navigation={navigation} route={route} />
      </TestApp>,
    )
    .toJSON()
  expect(tree).toMatchSnapshot()
})
