import { LitElement, html, css } from 'lit';
import { JsonRpc } from 'jsonrpc';

class QwcPi4j extends LitElement {
    static styles = css`
        .container {
            padding: 1rem;
        }
        h3 {
            margin-top: 0;
        }
        table {
            width: 100%;
            border-collapse: collapse;
        }
        th, td {
            text-align: left;
            padding: 0.4rem 0.8rem;
            border-bottom: 1px solid var(--lumo-contrast-10pct);
        }
        th {
            font-weight: bold;
            color: var(--lumo-secondary-text-color);
        }
        .badge-up {
            color: var(--lumo-success-color);
            font-weight: bold;
        }
        .badge-down {
            color: var(--lumo-error-color);
            font-weight: bold;
        }
    `;

    static properties = {
        _info: { state: true },
    };

    constructor() {
        super();
        this._info = null;
        this.jsonRpc = new JsonRpc(this);
    }

    connectedCallback() {
        super.connectedCallback();
        this.jsonRpc.getInfo().then(r => { this._info = r.result; });
    }

    render() {
        if (!this._info) {
            return html`<div class="container">Loading Pi4J info...</div>`;
        }
        const { enabled, mock, contextAvailable, platforms, providers } = this._info;
        return html`
            <div class="container">
                <h3>Pi4J Context</h3>
                <table>
                    <tr><th>Property</th><th>Value</th></tr>
                    <tr><td>Enabled</td><td>${enabled}</td></tr>
                    <tr><td>Mock mode</td><td>${mock}</td></tr>
                    <tr>
                        <td>Context</td>
                        <td class="${contextAvailable ? 'badge-up' : 'badge-down'}">
                            ${contextAvailable ? 'UP' : 'DOWN'}
                        </td>
                    </tr>
                </table>

                ${platforms && platforms.length > 0 ? html`
                    <h3>Platforms</h3>
                    <table>
                        <tr><th>ID</th><th>Name</th></tr>
                        ${platforms.map(p => html`<tr><td>${p.id}</td><td>${p.name}</td></tr>`)}
                    </table>` : ''}

                ${providers && providers.length > 0 ? html`
                    <h3>Providers</h3>
                    <table>
                        <tr><th>ID</th><th>Name</th><th>Type</th></tr>
                        ${providers.map(p => html`<tr><td>${p.id}</td><td>${p.name}</td><td>${p.type}</td></tr>`)}
                    </table>` : ''}
            </div>
        `;
    }
}

customElements.define('qwc-pi4j', QwcPi4j);
