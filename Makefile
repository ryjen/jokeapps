.PHONY: verify android-test rn-test

verify:
	python3 scripts/verify-layout.py
	python3 scripts/verify-release-signing.py
	python3 -m unittest discover -s scripts -p 'test_release_signing.py'

android-test:
	cd apps/android && ./gradlew test

rn-test:
	cd apps/react-native && yarn test --runInBand
