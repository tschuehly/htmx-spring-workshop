package de.tschuehly.easy.spring.auth.htmx;

import gg.jte.html.policy.PolicyGroup;
import gg.jte.html.policy.PreventInvalidAttributeNames;
import gg.jte.html.policy.PreventOutputInTagsAndAttributes;
import gg.jte.html.policy.PreventUnquotedAttributes;
import gg.jte.html.policy.PreventUppercaseTagsAndAttributes;

class JtePolicy extends PolicyGroup {

    JtePolicy() {
      addPolicy(new PreventUppercaseTagsAndAttributes());
      addPolicy(new PreventOutputInTagsAndAttributes(false));
      addPolicy(new PreventUnquotedAttributes());
      addPolicy(new PreventInvalidAttributeNames());
    }
  }