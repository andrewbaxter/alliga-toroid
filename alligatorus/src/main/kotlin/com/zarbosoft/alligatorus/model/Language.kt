package com.zarbosoft.alligatorus.model

import kotlin.Int
import kotlin.String

sealed class LanguageType

class Int(val value: Int) : LanguageType()

class String(val value: String) : LanguageType()

class Block(val statements: List<LanguageType>) : LanguageType()

class Scope(val child: LanguageType) : LanguageType()

class Vary(val child: LanguageType) : LanguageType()

class Reconst : LanguageType()

class Call(val target: LanguageType, val argument: LanguageType) : LanguageType()

class Access(val target: LanguageType, val argument: LanguageType) : LanguageType()

class Bind(val name: String, val value: LanguageType) : LanguageType()

class Builtin : LanguageType()

class Stage(val tree: LanguageType) : LanguageType()

class Lower(val tree: LanguageType) : LanguageType()

class Record(val elements: List<RecordElement>) : LanguageType()

class RecordElement(val key: LanguageType, val value: LanguageType) : LanguageType()

class Tuple(val elements: List<LanguageType>) : LanguageType()
