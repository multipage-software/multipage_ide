maclan(tag('AREA_NAME')).
maclan(tag('AREA_ID')).
maclan(tag('AREA_ALIAS')).
maclan(tag('SUBAREAS')).
maclan(tag('SUPERAREAS')).
maclan(tag('TAG')).
maclan(tag('LANG_FLAG')).
maclan(tag('LANG_DESCRIPTION')).
maclan(tag('LANG_ALIAS')).
maclan(tag('LANG_ID')).
maclan(tag('LANGUAGES')).
maclan(tag('RESOURCE_ID')).
maclan(tag('RESOURCE_EXT')).
maclan(tag('RESOURCE_VALUE')).
maclan(tag('VERSION_ANCHOR')).
maclan(tag('VERSION_URL')).
maclan(tag('VERSION_NAME')).
maclan(tag('VERSION_ID')).
maclan(tag('LIST')).
maclan(tag('LAST')).
maclan(tag('LOOP')).
maclan(tag('IMAGE')).
maclan(tag('VAR')).
maclan(tag('SET')).
maclan(tag('GET')).
maclan(tag('A')).
maclan(tag('ANCHOR')).
maclan(tag('REM')).
maclan(tag('IF')).
maclan(tag('ELSEIF')).
maclan(tag('ELSE')).
maclan(tag('PROCEDURE')).
maclan(tag('CALL')).
maclan(tag('PACK')).
maclan(tag('BLOCK')).
maclan(tag('OUTPUT')).
maclan(tag('TRACE')).
maclan(tag('BREAK')).
maclan(tag('PPTEXT')).
maclan(tag('URL')).
maclan(tag('USING')).
maclan(tag('RENDER_CLASS')).
maclan(tag('TIMOEOUT')).
maclan(tag('BOOKMARK')).
maclan(tag('REPLACE_BOOKMARK')).
maclan(tag('INCLUDE_ONCE')).
maclan(tag('LITERAL')).
maclan(tag('PRAGMA')).
maclan(tag('JAVASCRIPT')).
maclan(tag('INDENT')).
maclan(tag('NOINDENT')).
maclan(tag('CSS_LOOKUP_TABLE')).
maclan(tag('UNZIP')).
maclan(tag('RUN')).
maclan(tag('REDIRECT')).
maclan(tag('TRAY_MENU')).

maclan(tag('AREA_NAME'), property('areaId'), type('AreaId')).
maclan(tag('AREA_NAME'), property('areaAlias'), type('AreaAlias')).
maclan(tag('AREA_NAME'), property('area'), type('Area')).
maclan(tag('AREA_NAME'), property('startArea'), type('Area')).
maclan(tag('AREA_NAME'), property('homeArea'), type('Area')).
maclan(tag('AREA_NAME'), property('requestedArea'), type('Area')).
maclan(tag('AREA_NAME'), property('thisArea'), type('Area')).
maclan(tag('AREA_NAME'), property('areaSlot'), type('Area')).

maclan(tag('AREA_ID'), property('areaId'), type('AreaId')).
maclan(tag('AREA_ID'), property('areaAlias'), type('AreaAlias')).
maclan(tag('AREA_ID'), property('area'), type('Area')).
maclan(tag('AREA_ID'), property('startArea'), type('Area')).
maclan(tag('AREA_ID'), property('homeArea'), type('Area')).
maclan(tag('AREA_ID'), property('requestedArea'), type('Area')).
maclan(tag('AREA_ID'), property('thisArea'), type('Area')).
maclan(tag('AREA_ID'), property('areaSlot'), type('Area')).

maclan(tag('AREA_ALIAS'), property('areaId'), type('AreaId')).
maclan(tag('AREA_ALIAS'), property('areaAlias'), type('AreaAlias')).
maclan(tag('AREA_ALIAS'), property('area'), type('Area')).
maclan(tag('AREA_ALIAS'), property('startArea'), type('Area')).
maclan(tag('AREA_ALIAS'), property('homeArea'), type('Area')).
maclan(tag('AREA_ALIAS'), property('requestedArea'), type('Area')).
maclan(tag('AREA_ALIAS'), property('thisArea'), type('Area')).
maclan(tag('AREA_ALIAS'), property('areaSlot'), type('Area')).

maclan(tag('SUBAREAS'), property('list'), type('List')).
maclan(tag('SUBAREAS'), property('first'), type('Any')).
maclan(tag('SUBAREAS'), property('last'), type('Any')).
maclan(tag('SUBAREAS'), property('cond'), type('Boolean')).
maclan(tag('SUBAREAS'), property('reversed'), type('Void')).
maclan(tag('SUBAREAS'), property('areaId'), type('AreaId')).
maclan(tag('SUBAREAS'), property('areaAlias'), type('AreaAlias')).
maclan(tag('SUBAREAS'), property('area'), type('Area')).
maclan(tag('SUBAREAS'), property('startArea'), type('Area')).
maclan(tag('SUBAREAS'), property('homeArea'), type('Area')).
maclan(tag('SUBAREAS'), property('requestedArea'), type('Area')).
maclan(tag('SUBAREAS'), property('thisArea'), type('Area')).
maclan(tag('SUBAREAS'), property('areaSlot'), type('Area')).

maclan(tag('SUPERAREAS'), property('list'), type('List')).
maclan(tag('SUPERAREAS'), property('first'), type('Any')).
maclan(tag('SUPERAREAS'), property('last'), type('Any')).
maclan(tag('SUPERAREAS'), property('cond'), type('Boolean')).
maclan(tag('SUPERAREAS'), property('reversed', type('Void'))).
maclan(tag('SUPERAREAS'), property('areaId'), type('AreaId')).
maclan(tag('SUPERAREAS'), property('areaAlias'), type('AreaAlias')).
maclan(tag('SUPERAREAS'), property('area'), type('Area')).
maclan(tag('SUPERAREAS'), property('startArea'), type('Area')).
maclan(tag('SUPERAREAS'), property('homeArea'), type('Area')).
maclan(tag('SUPERAREAS'), property('requestedArea'), type('Area')).
maclan(tag('SUPERAREAS'), property('thisArea'), type('Area')).
maclan(tag('SUPERAREAS'), property('areaSlot'), type('Area')).

maclan(tag('TAG'), property('slot'), type('String')).
maclan(tag('TAG'), property('local'), type('Void')).
maclan(tag('TAG'), property('skipDefault'), type('Void')).
maclan(tag('TAG'), property('parent'), type('Void')).
maclan(tag('TAG'), property('enableSpecialValue'), type('Void')).
maclan(tag('TAG'), property('areaId'), type('AreaId')).
maclan(tag('TAG'), property('areaAlias'), type('AreaAlias')).
maclan(tag('TAG'), property('area'), type('Area')).
maclan(tag('TAG'), property('startArea'), type('Area')).
maclan(tag('TAG'), property('homeArea'), type('Area')).
maclan(tag('TAG'), property('requestedArea'), type('Area')).
maclan(tag('TAG'), property('thisArea'), type('Area')).
maclan(tag('TAG'), property('areaSlot'), type('Area')).

maclan(tag('LANGUAGES'), property('divider'), type('String')).
maclan(tag('LANGUAGES'), property('transparent'), type('Void')).

maclan(tag('RESOURCE_ID'), property('res'), type('String')).
maclan(tag('RESOURCE_ID'), property('render')).
maclan(tag('RESOURCE_ID'), property('areaId'), type('AreaId')).
maclan(tag('RESOURCE_ID'), property('areaAlias'), type('AreaAlias')).
maclan(tag('RESOURCE_ID'), property('area'), type('Area')).
maclan(tag('RESOURCE_ID'), property('startArea'), type('Area')).
maclan(tag('RESOURCE_ID'), property('homeArea'), type('Area')).
maclan(tag('RESOURCE_ID'), property('requestedArea'), type('Area')).
maclan(tag('RESOURCE_ID'), property('thisArea'), type('Area')).
maclan(tag('RESOURCE_ID'), property('areaSlot'), type('Area')).

maclan(tag('RESOURCE_EXT'), property('res')).
maclan(tag('RESOURCE_EXT'), property('render')).
maclan(tag('RESOURCE_EXT'), property('areaId'), type('AreaId')).
maclan(tag('RESOURCE_EXT'), property('areaAlias'), type('AreaAlias')).
maclan(tag('RESOURCE_EXT'), property('area'), type('Area')).
maclan(tag('RESOURCE_EXT'), property('startArea'), type('Area')).
maclan(tag('RESOURCE_EXT'), property('homeArea'), type('Area')).
maclan(tag('RESOURCE_EXT'), property('requestedArea'), type('Area')).
maclan(tag('RESOURCE_EXT'), property('thisArea'), type('Area')).
maclan(tag('RESOURCE_EXT'), property('areaSlot'), type('Area')).

maclan(tag('RESOURCE_VALUE'), property('res')).
maclan(tag('RESOURCE_VALUE'), property('render')).
maclan(tag('RESOURCE_VALUE'), property('coding')).
maclan(tag('RESOURCE_VALUE'), property('areaId'), type('AreaId')).
maclan(tag('RESOURCE_VALUE'), property('areaAlias'), type('AreaAlias')).
maclan(tag('RESOURCE_VALUE'), property('area'), type('Area')).
maclan(tag('RESOURCE_VALUE'), property('startArea'), type('Area')).
maclan(tag('RESOURCE_VALUE'), property('homeArea'), type('Area')).
maclan(tag('RESOURCE_VALUE'), property('requestedArea'), type('Area')).
maclan(tag('RESOURCE_VALUE'), property('thisArea'), type('Area')).
maclan(tag('RESOURCE_VALUE'), property('areaSlot'), type('Area')).

maclan(tag('VERSION_URL'), property('areaId'), type('AreaId')).
maclan(tag('VERSION_URL'), property('areaAlias'), type('AreaAlias')).
maclan(tag('VERSION_URL'), property('area'), type('Area')).
maclan(tag('VERSION_URL'), property('startArea'), type('Area')).
maclan(tag('VERSION_URL'), property('homeArea'), type('Area')).
maclan(tag('VERSION_URL'), property('requestedArea'), type('Area')).
maclan(tag('VERSION_URL'), property('thisArea'), type('Area')).
maclan(tag('VERSION_URL'), property('areaSlot'), type('Area')).

maclan(tag('LIST'), property('list'), type('List')).
maclan(tag('LIST'), property('iterator')).
maclan(tag('LIST'), property('item')).
maclan(tag('LIST'), property('divider')).
maclan(tag('LIST'), property('local')).
maclan(tag('LIST'), property('break')).
maclan(tag('LIST'), property('discard')).
maclan(tag('LIST'), property('transparent')).

maclan(tag('LAST'), property('discard')).

maclan(tag('LOOP'), property('count')).
maclan(tag('LOOP'), property('divider')).
maclan(tag('LOOP'), property('index')).
maclan(tag('LOOP'), property('break')).
maclan(tag('LOOP'), property('discard')).
maclan(tag('LOOP'), property('from')).
maclan(tag('LOOP'), property('to')).
maclan(tag('LOOP'), property('step')).
maclan(tag('LOOP'), property('transparent')).

maclan(tag('IMAGE'), property('res')).
maclan(tag('IMAGE'), property('areaId'), type('AreaId')).
maclan(tag('IMAGE'), property('areaAlias'), type('AreaAlias')).
maclan(tag('IMAGE'), property('area'), type('Area')).
maclan(tag('IMAGE'), property('startArea'), type('Area')).
maclan(tag('IMAGE'), property('homeArea'), type('Area')).
maclan(tag('IMAGE'), property('requestedArea'), type('Area')).
maclan(tag('IMAGE'), property('thisArea'), type('Area')).
maclan(tag('IMAGE'), property('areaSlot'), type('Area')).

maclan(tag('GET'), property('exp')).

maclan(tag('A'), property('href')).
maclan(tag('A'), property('areaId'), type('AreaId')).
maclan(tag('A'), property('areaAlias'), type('AreaAlias')).
maclan(tag('A'), property('area'), type('Area')).
maclan(tag('A'), property('startArea'), type('Area')).
maclan(tag('A'), property('homeArea'), type('Area')).
maclan(tag('A'), property('requestedArea'), type('Area')).
maclan(tag('A'), property('thisArea'), type('Area')).
maclan(tag('A'), property('areaSlot'), type('Area')).

maclan(tag('ANCHOR'), property('href')).
maclan(tag('ANCHOR'), property('areaId'), type('AreaId')).
maclan(tag('ANCHOR'), property('areaAlias'), type('AreaAlias')).
maclan(tag('ANCHOR'), property('area'), type('Area')).
maclan(tag('ANCHOR'), property('startArea'), type('Area')).
maclan(tag('ANCHOR'), property('homeArea'), type('Area')).
maclan(tag('ANCHOR'), property('requestedArea'), type('Area')).
maclan(tag('ANCHOR'), property('thisArea'), type('Area')).
maclan(tag('ANCHOR'), property('areaSlot'), type('Area')).

maclan(tag('IF'), property('cond'), type('Boolean')).
maclan(tag('ELSEIF'), property('cond'), type('Boolean')).

maclan(tag('PROCEDURE'), property('name'), type('String')).
maclan(tag('PROCEDURE'), property('$name')).
maclan(tag('PROCEDURE'), property('$useLast')).
maclan(tag('PROCEDURE'), property('$global')).
maclan(tag('PROCEDURE'), property('$returnText')).
maclan(tag('PROCEDURE'), property('$inner')).
maclan(tag('PROCEDURE'), property('$transparent')).

maclan(tag('CALL'), property('name'), type('String')).
maclan(tag('CALL'), property('$name')).
maclan(tag('CALL'), property('$areaId')).
maclan(tag('CALL'), property('$areaAlias')).
maclan(tag('CALL'), property('$area')).
maclan(tag('CALL'), property('$startArea')).
maclan(tag('CALL'), property('$homeArea')).
maclan(tag('CALL'), property('$requestedArea')).
maclan(tag('CALL'), property('$thisArea')).
maclan(tag('CALL'), property('$areaSlot')).
maclan(tag('CALL'), property('$parent')).
maclan(tag('CALL'), property('$inner')).

maclan(tag('PACK'), property('strong')).
maclan(tag('PACK'), property('trim')).
maclan(tag('PACK'), property('trimBegin')).
maclan(tag('PACK'), property('trimEnd')).

maclan(tag('BLOCK'), property('areaId'), type('AreaId')).
maclan(tag('BLOCK'), property('areaAlias'), type('AreaAlias')).
maclan(tag('BLOCK'), property('area'), type('Area')).
maclan(tag('BLOCK'), property('startArea'), type('Area')).
maclan(tag('BLOCK'), property('homeArea'), type('Area')).
maclan(tag('BLOCK'), property('requestedArea'), type('Area')).
maclan(tag('BLOCK'), property('thisArea'), type('Area')).
maclan(tag('BLOCK'), property('areaSlot'), type('Area')).
maclan(tag('BLOCK'), property('transparent')).

maclan(tag('TRACE'), property('name'), type('String')).
maclan(tag('TRACE'), property('simple')).

maclan(tag('BREAK'), property('name'), type('String')).
maclan(tag('BREAK'), property('no')).

maclan(tag('URL'), property('areaId'), type('AreaId')).
maclan(tag('URL'), property('areaAlias'), type('AreaAlias')).
maclan(tag('URL'), property('area'), type('Area')).
maclan(tag('URL'), property('startArea'), type('Area')).
maclan(tag('URL'), property('homeArea'), type('Area')).
maclan(tag('URL'), property('requestedArea'), type('Area')).
maclan(tag('URL'), property('thisArea'), type('Area')).
maclan(tag('URL'), property('areaSlot'), type('Area')).
maclan(tag('URL'), property('res')).
maclan(tag('URL'), property('localhost')).
maclan(tag('URL'), property('langAlias')).
maclan(tag('URL'), property('versionAlias')).
maclan(tag('URL'), property('download')).
maclan(tag('URL'), property('file')).

maclan(tag('USING'), property('res')).
maclan(tag('USING'), property('resId')).
maclan(tag('USING'), property('file')).
maclan(tag('USING'), property('extract')).
maclan(tag('USING'), property('encoding')).
maclan(tag('USING'), property('areaId'), type('AreaId')).
maclan(tag('USING'), property('areaAlias'), type('AreaAlias')).
maclan(tag('USING'), property('area'), type('Area')).
maclan(tag('USING'), property('startArea'), type('Area')).
maclan(tag('USING'), property('homeArea'), type('Area')).
maclan(tag('USING'), property('requestedArea'), type('Area')).
maclan(tag('USING'), property('thisArea'), type('Area')).
maclan(tag('USING'), property('areaSlot'), type('Area')).

maclan(tag('PRAGMA'), property('php')).
maclan(tag('PRAGMA'), property('tabulator')).
maclan(tag('PRAGMA'), property('webInterface')).
maclan(tag('PRAGMA'), property('metaCharset')).

maclan(tag('UNZIP'), property('areaId'), type('AreaId')).
maclan(tag('UNZIP'), property('areaAlias'), type('AreaAlias')).
maclan(tag('UNZIP'), property('area'), type('Area')).
maclan(tag('UNZIP'), property('startArea'), type('Area')).
maclan(tag('UNZIP'), property('homeArea'), type('Area')).
maclan(tag('UNZIP'), property('requestedArea'), type('Area')).
maclan(tag('UNZIP'), property('thisArea'), type('Area')).
maclan(tag('UNZIP'), property('res')).
maclan(tag('UNZIP'), property('folder')).

maclan(tag('RUN'), property('cmd')).
maclan(tag('RUN'), property('output')).
maclan(tag('RUN'), property('exception')).

maclan(tag('REDIRECT'), property('uri')).

maclan(tag('TRAY_MENU'), property('name'), type('String')).
maclan(tag('TRAY_MENU'), property('url')).
