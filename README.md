# VirtualCard

## Commands

* Json command format (not implemented yet for custom command) :

```json
{
	"commands": [
		{
			"name": "first_command", 
			"description": "command description",
			"help": "help message for this command",
			"alias": ["alias1", "alias2"]
		},
		{
			"name": "second_command",
			"description": "second command description",
			"help": "help message for this second command",
			"alias": ["second_alias1", "second_alias2"]	
		}

	]
}
```

Attributes "name" and "description" are obligatory.
If a name as already take you cannot use it a second time in another name or in alias.