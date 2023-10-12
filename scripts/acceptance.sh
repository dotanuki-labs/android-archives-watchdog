#! /usr/bin/env bash

set -euo pipefail

dir="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "${dir%/*}"

readonly arw="build/bin/arw"

test_no_arguments_passed() {
  "$arw" | grep "Usage: arw <command> <options>" >/dev/null
}

echo
echo "→ Building binary"
./scripts/package.sh

echo "→ Running tests"
echo

test_no_arguments_passed

echo "🔥 Success"
echo
