#! /usr/bin/env bash

set -euo pipefail

dir="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "${dir%/*}"

readonly arw="build/bin/arw"
readonly fixtures="src/test/resources"

test_usage() {
  "$arw" | grep "Usage" >/dev/null
}

test_invalid_inputs() {
  comparison=$("$arw" overview -t "$fixtures/missing.apk" -b "$toml" || true)
  echo "$comparison" | grep "missing.apk does not exist" >/dev/null
}

test_overview() {

  echo "✔ Testing artifact overview"

  "$arw" overview --target="$fixtures/app-debug.apk"

  overview=$("$arw" overview --target="$fixtures/app-debug.apk" --json)
  (( $(echo "$overview" | jq '.min_sdk') == 28 ))
  (( $(echo "$overview" | jq '.target_sdk') == 33 ))
}

test_generate_baseline() {
  echo "✔ Testing baseline generation"

  "$arw" generate --target="$fixtures/app-debug.apk"

  local toml="io.dotanuki.norris.android.debug.toml"

  echo
  cat "$toml"
  echo

  rm -rf "$toml"
}

test_compare_baseline_with_artifact() {
  echo
  echo "✔ Testing comparison between baseline and artifacts"

  "$arw" generate --target="$fixtures/app-release.aab"

  local toml="io.dotanuki.norris.android.toml"
  comparison=$("$arw" compare -t "$fixtures/app-release-changed.apk" -b "$toml" || true)
  echo "$comparison"
  echo "$comparison" | grep "Your baseline file does not match the supplied artifact" >/dev/null

  rm -rf "$toml"
}

echo
echo "→ Building binary"
./scripts/package.sh

echo "→ Running tests"
echo

test_usage
test_overview
test_generate_baseline
test_compare_baseline_with_artifact

echo
echo "🔥 Success"
echo
