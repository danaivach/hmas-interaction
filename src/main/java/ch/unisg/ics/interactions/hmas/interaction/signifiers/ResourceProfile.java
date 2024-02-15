package ch.unisg.ics.interactions.hmas.interaction.signifiers;

import ch.unisg.ics.interactions.hmas.core.hostables.Artifact;
import ch.unisg.ics.interactions.hmas.core.hostables.ProfiledResource;
import ch.unisg.ics.interactions.hmas.core.hostables.BaseResourceProfile;
import com.google.common.collect.ImmutableSet;

import java.util.HashSet;
import java.util.Set;

public class ResourceProfile extends BaseResourceProfile {

  private final Set<Signifier> signifiers;

  protected ResourceProfile(AbstractBuilder builder) {
    super(builder);
    this.signifiers = ImmutableSet.copyOf(builder.signifiers);
  }

  public Set<Signifier> getExposedSignifiers() {
    return this.signifiers;
  }

  public static class Builder extends AbstractBuilder<Builder, ResourceProfile> {

    public Builder(ProfiledResource resource) {
      super(resource);
    }

    public ResourceProfile build() {
      return new ResourceProfile(this);
    }
  }

  public abstract static class AbstractBuilder<S extends AbstractBuilder, T extends BaseResourceProfile>
          extends BaseResourceProfile.AbstractBuilder<S, T> {

    private final Set<Signifier> signifiers;

    public AbstractBuilder(ProfiledResource resource) {
      super(resource);
      this.signifiers = new HashSet<>();
    }

    public S exposeSignifier(Signifier signifier) {
      this.signifiers.add(signifier);
      return (S) this;
    }

    public S exposeSignifiers(Set<Signifier> signifiers) {
      this.signifiers.addAll(signifiers);
      return (S) this;
    }

    public abstract T build();
  }
}
