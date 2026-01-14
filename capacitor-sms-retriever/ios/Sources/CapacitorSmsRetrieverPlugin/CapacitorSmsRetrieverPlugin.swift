import Foundation
import Capacitor

/**
 * Please read the Capacitor iOS Plugin Development Guide
 * here: https://capacitorjs.com/docs/plugins/ios
 */
@objc(CapacitorSmsRetrieverPlugin)
public class CapacitorSmsRetrieverPlugin: CAPPlugin, CAPBridgedPlugin {
    public let identifier = "CapacitorSmsRetrieverPlugin"
    public let jsName = "CapacitorSmsRetriever"
    public let pluginMethods: [CAPPluginMethod] = [
        CAPPluginMethod(name: "echo", returnType: CAPPluginReturnPromise)
    ]
    private let implementation = CapacitorSmsRetriever()

    @objc func echo(_ call: CAPPluginCall) {
        let value = call.getString("value") ?? ""
        call.resolve([
            "value": implementation.echo(value)
        ])
    }
}
