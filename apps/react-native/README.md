# jokeapp

a small app to request a joke from [iCanHazDadJoke](https://icanhazdadjoke.com)

| Random                                               | Favorites                                               |
| ---------------------------------------------------- | ------------------------------------------------------- |
| <img src="docs/screenshots/random.png" height="380"> | <img src="docs/screenshots/favorites.png" height="380"> |

## analysis

### requirements

- random joke in big font
- action button to add/remove favourites
- action button to share joke
- bottom buttons to sync and view favourites
- favourites a card list

## design

### architecture

![DDD](https://resocoder.com/wp-content/uploads/2020/03/DDD-Flutter-Diagram-v3.svg)

- uncle bob's clean architecture, and domain driven design
- separation of concerns
- Dependency injection
- redux architecture
- RTK query instead of fetch

## implementation

### project layout

#### clean architecture modules

- **application**: storage and use cases, binding layers
- **domain**: business entities, interfaces, business critical logic
- **infrastructure**: implementation of repositories, data sources and mapping to domain
- **presentation**: presentation of data and user interaction

### tests

- rules and/or injection to handle threading and coroutines
- fake data generator
- mocking and mock network server
- in-memory database
- user interface tag verifications

## maintenance

### TODO

- [ ] improve UX
- [ ] ML to know sentiment of joke and predict favorites
- [ ] speech bubble characters based on category or sentiment
- [ ] abstract to project template
- [ ] add more remote sources for all the jokes
- [ ] export to text-over-photo for sharing
- [ ] analytics
- [ ] allow user to add content
- [ ] theme switcher
- [ ] device home screen widget
- [ ] splash screen and logo
- [ ] Text-to-speech, laugh track
- [ ] Animations
- [ ] Monetize user customizations
- [ ] security analysis

## Android signing boundary

The tracked `android/app/debug.keystore` is a public Android development fixture, **not** a production signing identity. Debug builds continue to use it. The React Native `release` build type intentionally has **no signingConfig**: it generates an unsigned artifact until an operator supplies a privately managed release identity and reviews the CI/distribution process. Never use `signingConfigs.debug` for production or share a release keystore in source control. The root `make verify` and manual qualification workflow enforce that rule with a focused regression test.

This protects future releases; it cannot establish whether an older debug-signed build was distributed. If any public app was previously distributed with the debug key, identify its package IDs and plan a supported app-signing migration before attempting updates.
