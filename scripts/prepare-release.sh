#! /usr/bin/env bash

set -euo pipefail

dir="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "${dir%/*}"

readonly binary="build/bin/aaw"

if [[ -n "$CI" ]]; then
    version=$(grep "latest" src/main/resources/versions.properties | sed "s/latest=//g")
    echo "version=$version" >>"$GITHUB_OUTPUT"

    if ! test -f "$binary"; then
        echo "✗ Error : missing target binary!"
        echo
        exit 1
    fi

    zip -j "aaw-$version.zip" "$binary"
fi
