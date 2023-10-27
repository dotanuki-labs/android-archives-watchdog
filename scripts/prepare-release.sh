#! /usr/bin/env bash

set -euo pipefail

dir="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "${dir%/*}"

if [[ -n "$CI" ]]; then
    version=$(grep "latest" src/main/resources/versions.properties | sed "s/latest=//g")
    echo "version=$version" >>"$GITHUB_OUTPUT"
fi
