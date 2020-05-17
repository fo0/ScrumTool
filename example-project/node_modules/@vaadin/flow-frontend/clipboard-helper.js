import '@polymer/polymer/polymer-legacy.js';
import { html } from '@polymer/polymer/lib/utils/html-tag.js';
import { PolymerElement } from '@polymer/polymer/polymer-element.js';
class ClipboardHelper extends PolymerElement {
  static get template() {
    return html`
   <div id="wrapper" on-click="copy"></div> 
`;
  }

  static get is() {
      return 'clipboard-helper';
  }

  static get properties() {
      return {
          content: {
              type: String,
              value: ""
          }
      };
  }
  copy() {
      const el = document.createElement('textarea');
      el.value = this.content;
      el.setAttribute('readonly', '');
      el.style.position = 'absolute';
      el.style.left = '-9999px';
      document.body.appendChild(el);
      const selected =
          document.getSelection().rangeCount > 0
              ? document.getSelection().getRangeAt(0)
              : false;
      el.select();
      document.execCommand('copy');
      document.body.removeChild(el);
      if (selected) {
          document.getSelection().removeAllRanges();
          document.getSelection().addRange(selected);
      }
  }
}
customElements.define(ClipboardHelper.is, ClipboardHelper);

