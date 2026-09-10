package praktikum;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.Collection;

import static org.mockito.Mockito.*;

@RunWith(Parameterized.class)
public class BurgerPriceTest {

    private final float bunPrice;
    private final float fillingPrice;
    private final float saucePrice;
    private final float extraPrice;
    private final float expectedTotal;

    @Parameterized.Parameters(name = "bun={0}, filling={1}, sauce={2}, extra={3} => total={4}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {100f, 50f, 30f, 70f, 100 * 2 + 50 + 30 + 70},
                {200f, 0f, 0f, 0f, 200 * 2},
                {50f, 10f, 20f, 0f, 50 * 2 + 10 + 20}
        });
    }

    public BurgerPriceTest(float bunPrice, float fillingPrice, float saucePrice,
                           float extraPrice, float expectedTotal) {
        this.bunPrice = bunPrice;
        this.fillingPrice = fillingPrice;
        this.saucePrice = saucePrice;
        this.extraPrice = extraPrice;
        this.expectedTotal = expectedTotal;
    }

    @Test
    public void getPriceShouldReturnCorrectSum() {
        Bun bunStub = mock(Bun.class);
        when(bunStub.getPrice()).thenReturn(bunPrice);

        Ingredient filling = mock(Ingredient.class);
        when(filling.getPrice()).thenReturn(fillingPrice);

        Ingredient sauce = mock(Ingredient.class);
        when(sauce.getPrice()).thenReturn(saucePrice);

        Ingredient extra = mock(Ingredient.class);
        when(extra.getPrice()).thenReturn(extraPrice);

        Burger burger = new Burger();
        burger.setBuns(bunStub);

        // Добавляем все ингредиенты (даже с нулевой ценой) — это допустимо
        burger.addIngredient(filling);
        burger.addIngredient(sauce);
        burger.addIngredient(extra);

        float actual = burger.getPrice();
        Assert.assertEquals("Цена рассчитана неверно", expectedTotal, actual, 0.001f);
    }
}