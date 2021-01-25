---
layout: docs-core
title: Moore
permalink: /arrow/ui/moore/
---

## Moore





A `Moore` machine is a [comonadic]({{ '/arrow/typeclasses/comonad' | relative_url }}) data structure that holds a state and, in order to change it, we need to dispatch events of some specific type. This approach is similar to the [_Elm architecture_](https://guide.elm-lang.org/architecture/) or [_Redux_](https://redux.js.org).

For creating a `Moore` machine, we need its initial state and a `handle` function that will determine the inputs it can accept and how the state will change with each one.

We also have an `extract` function that returns the current state, and a `coflatMap` that transforms its type.
