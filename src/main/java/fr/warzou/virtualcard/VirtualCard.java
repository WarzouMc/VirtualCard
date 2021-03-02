package fr.warzou.virtualcard;

import fr.warzou.virtualcard.api.Card;

public class VirtualCard {

    public static void main(String[] args) {
        Card card = new Card();
        card.start();
        //not real api so wait end like that
        while (card.isEnabled());
    }

}
