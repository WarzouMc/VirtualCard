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

## Module list

```json
{
  "modules": [
    {
      "name": "name1",
      "main": "module main file"
    },
    {
    	"name": "name2",
    	"main": "module main file"
    }
  ]
}
```

All sections are obligatory.


## Main module file


This is too recent to be explain and is not clearly defined so this is just a concrete application.
```json
{
  "module_name": "clock",
  "stream_type": 1,
  "packets": {
    "environment": {
      "out.system.clock": "system.clock",
      "out.system.clock.now": "system.clock.now"
    }
  }
}
```

No rules currently.