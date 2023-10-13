#! /usr/bin/env bash

set -euo pipefail

dir="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "${dir%/*}"

readonly arw="build/bin/arw"

test_no_arguments_passed() {
  "$arw" | grep "Usage" >/dev/null
}

test_overview_console() {
  "$arw" overview
}

test_overview_values() {
  overview=$("$arw" overview --json)
  (( $(echo "$overview" | jq '.min_sdk') == 28 ))
  (( $(echo "$overview" | jq '.target_sdk') == 33 ))
}

echo
echo "→ Building binary"
./scripts/package.sh

echo "→ Running tests"
echo

test_no_arguments_passed
test_overview_console
test_overview_values

echo "🔥 Success"
echo
