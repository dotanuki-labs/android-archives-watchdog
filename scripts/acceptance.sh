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
  comparison=$("$arw" overview -a "$fixtures/missing.apk" -b "$toml" || true)
  echo "$comparison" | grep "missing.apk does not exist" >/dev/null
}

test_overview() {

  echo "✔ Testing artifact overview"

  "$arw" overview --archive="$fixtures/app-debug.apk"

  overview=$("$arw" overview --archive="$fixtures/app-debug.apk" --json)
  (( $(echo "$overview" | jq '.min_sdk') == 28 ))
  (( $(echo "$overview" | jq '.target_sdk') == 33 ))
}

test_generate_baseline_complete() {
  echo "✔ Testing baseline generation (complete)"

  "$arw" generate -a "$fixtures/app-debug.apk"

  local complete_toml="io.dotanuki.norris.android.debug.toml"

  echo
  cat "$complete_toml"
  echo

  rm -rf "$complete_toml"
}

test_generate_baseline_compact() {
  echo "✔ Testing baseline generation (compact)"

  "$arw" generate --archive="$fixtures/app-debug.apk" --trust="io.dotanuki"

  local compact_toml="io.dotanuki.norris.android.debug.toml"

  echo
  cat "$compact_toml"
  echo

  rm -rf "$compact_toml"
}

test_compare_baseline_with_artifact() {
  echo
  echo "✔ Testing comparison between baseline and artifacts"

  "$arw" generate --archive="$fixtures/app-release.aab" --trust="io.dotanuki"

  local toml="io.dotanuki.norris.android.toml"
  comparison=$("$arw" compare -a "$fixtures/app-release-changed.apk" -b "$toml" || true)
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
test_generate_baseline_complete
test_generate_baseline_compact
test_compare_baseline_with_artifact

echo
echo "🔥 Success"
echo
