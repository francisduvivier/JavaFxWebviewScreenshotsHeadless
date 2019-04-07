#!/usr/bin/env bash
./scripts/compile-java.sh
cd out/production/JavaFXScreenshotProject/
jar cfm ../../javaFXScreenshots.jar ../../../MANIFEST.MF ./*
