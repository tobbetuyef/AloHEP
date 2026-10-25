#!/bin/bash

set -e
cd "$(dirname "$0")"

VERSION="2.0"

rm -rf bin
bash build.sh

rm -rf dist
mkdir -p dist/AloHEP
printf 'Main-Class: org.hepforge.alohep.Launcher\nClass-Path: gson-2.9.1.jar\nImplementation-Title: AloHEP\nImplementation-Version: %s\n' "$VERSION" > dist/manifest.txt
jar cfm dist/AloHEP/AloHEP.jar dist/manifest.txt -C bin .
cp gson-2.9.1.jar LICENSE LICENSE-gson.txt README.md dist/AloHEP/
cp -r save dist/AloHEP/
mkdir -p dist/AloHEP/test_run
cp test_run/run_sample.sh test_run/expected_output.txt dist/AloHEP/test_run/
jar cfM "dist/AloHEP-$VERSION.zip" -C dist AloHEP
rm dist/manifest.txt

# Source package (the committed tree) for the CPC Program Library.
if git rev-parse --is-inside-work-tree >/dev/null 2>&1; then
    git archive --format=zip --prefix="AloHEP-$VERSION-source/" -o "dist/AloHEP-$VERSION-source.zip" HEAD
fi

echo "Release $VERSION written to dist/AloHEP-$VERSION.zip"
[ -f "dist/AloHEP-$VERSION-source.zip" ] && echo "Source package written to dist/AloHEP-$VERSION-source.zip"
echo "  GUI : java -jar AloHEP.jar   (run inside the extracted AloHEP folder, next to save/)"
echo "  CLI : java -cp AloHEP.jar org.hepforge.alohep.HeadlessLauncher ..."
echo "  Test: bash test_run/run_sample.sh"
