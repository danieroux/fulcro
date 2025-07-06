const {test} = require('@playwright/test');

const randomDelay = (min, max) => Math.floor(Math.random() * (max - min + 1)) + min;

test('Repeatedly click routing buttons in nested-routing-hooks-demo until the wrong ident is calculated', async ({page}) => {
    const maxAttempts = 1000;
    let attempt = 0;

    await page.goto('http://localhost:9001');

    // This is a Not Great handle
    await page.locator('div:nth-child(11) > .nubank_workspaces_ui_WorkspacesRoot__ns-header > .nubank_workspaces_ui_WorkspacesRoot__expand-arrow').click();
    await page.getByText('nested-routing-hooks-demo').first().click();
    await page.getByRole('img').click();
    await page.getByRole('button', {name: 'Solo'}).click();

    while (attempt < maxAttempts) {
        attempt++;
        console.log(`Attempt ${attempt}`);

        const errorSnitcher = page.locator('div#errorsnitcher');
        if (await errorSnitcher.isVisible()) {
            console.log(`Error snitcher appeared on attempt ${attempt}`);
            await page.pause();
            return; // Test passed!
        }

        const buttons = await page.locator('button.router-button').all();
        const randomButton = buttons[Math.floor(Math.random() * buttons.length)];

        console.log(`Clicking button: ${randomButton}`);
        await randomButton.click();
        await page.waitForTimeout(randomDelay(100, 400));
    }

    throw new Error("Did not get the error snitcher");
});