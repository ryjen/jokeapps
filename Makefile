.PHONY: verify android-test rn-test

verify:
	python3 scripts/verify-layout.py

android-test:
	cd apps/android && ./gradlew test

rn-test:
	cd apps/react-native && yarn test --runInBand
