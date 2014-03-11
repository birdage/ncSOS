#!/bin/bash


echo "Building PostSOS..."
echo ""
echo "Directory:" 
pwd
echo ""
echo "Making Zip..."
rm ./release/postSOS-1.0.0.zip
zip -j ./release/postSOS-1.0.0.zip ./target/postSOS-1.0.jar
zip -r ./release/postSOS-1.0.0.zip ./sos_resources/*
zip -j ./release/postSOS-1.0.0.zip ./jar/*
echo ""
echo "Build Complete PostSOS..."