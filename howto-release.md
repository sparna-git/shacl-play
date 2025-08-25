1. Make sure Git is uptodate and on version master
2. Update version:
	mvn versions:set -DnewVersion=0.8.1
	mvn versions:commit
3. Commit and push
4. Compile
5. Adjust version in deploy.sh
5. Deploy with deploy.sh
6. Delete deployment in Visual Studio
6. Lists latest issues: https://github.com/sparna-git/shacl-play/issues?q=is%3Aissue+is%3Aclosed+sort%3Aupdated-desc
7. Create release on Github, template:


```

## Major New Features

_none_

## Other Enhancements

_none_

## Bug fixes

_none_

## Refactoring

_ none_

## Documentation

_none_	
```
